/**
 * [파일 명] facilityMng.js
 */

(() => {

	const FacilityMng = {
		popupRefs: {
			erForm: null,
			adMng: null,
			erCntr: null,
			erMa: null, 
		}, 
		isFacilityHidden: true,
		tempErViewType: 0
	};

	// 장비번호/IP 컬럼 숨김/표시
	FacilityMng.toggleFacilityId = function () {
		const headers = ['th:nth-child(7)', 'th:nth-child(8)'];
		const cells = ['td:nth-child(7)', 'td:nth-child(8)'];
		FacilityMng.isFacilityHidden = !FacilityMng.isFacilityHidden;
		headers.concat(cells).forEach(selector => {
			document.querySelectorAll(selector).forEach(el => {
				el.classList.toggle('hidden', FacilityMng.isFacilityHidden);
			});
		});
	};

	FacilityMng.clearInput = function (inputId) {
		const $input = $('#' + inputId);
		$input.val('');

		if (inputId === 'roadlineSearch') {
			$input.removeAttr('data-routeid data-routever');
			$('#roadline').hide();
		} else if (inputId === 'carInfoSearch') {
			$input.removeAttr('data-vehid');
			$('#carInfo').hide();
		}
	};

	// 전체 선택 토글
	FacilityMng.toggleSelectAll = function (selectAllCkbox) {
		document.querySelectorAll('input.selErId').forEach(checkbox => {
			checkbox.checked = selectAllCkbox.checked;
		});
	}		

	function getStatusText(statCd) {
		const STATUS = {
			'0': '정상', '1': '리부팅', '2': '화면OFF', '3': '점검중화면',
			'4': 'Fault', '5': '통신두절', '6': '테스트화면', '7': '긴급메시지'
		};
		return STATUS[statCd] || '';
	}

	function loadFile(attachCode, imgIndex) {
		if (!attachCode) return;		
		const url = `https://bims.sejong.go.kr/user-service/api/v1/images/editor/${attachCode}`;
		const er_img = document.getElementById(imgIndex);
		fetch(url)
			.then(response => {
				if (!response.ok) throw new Error('이미지를 불러오는데 실패했습니다.');
				return response.blob();
			})
			.then(blob => {
				if (blob.type.startsWith('image/')) {
					const imgUrl = URL.createObjectURL(blob);
					er_img.src = imgUrl;
					er_img.style.display = 'block';
				} else {
					throw new Error('유효하지 않은 이미지 형식입니다.');
				}
			})
			.catch(error => {
				console.error('Error loading file:', error);
				// alert("파일을 불러오는 중 오류가 발생했습니다.");
			});
	}

	FacilityMng.openPopupOnce = function (refKey, name, url, options) {
		const popup = window.open('', name, options);
		if (!popup || popup.closed || typeof popup.closed === 'undefined') {
			alert('팝업을 열 수 없습니다. 팝업 차단을 확인해주세요.');
			return null;
		}
		
		if (FacilityMng.popupRefs[refKey] && !FacilityMng.popupRefs[refKey].closed) {
			FacilityMng.popupRefs[refKey].focus();
			alert(`${name} 창이 이미 열려 있습니다.`);
			return null;
		}

		FacilityMng.popupRefs[refKey] = popup;
		if (url) popup.location.href = url;
		return popup;
	}

	function bindMiscEventListeners() {
		$(document).on('mousemove click', function (event) {
			if (!$(event.target).closest('#table-body').length) {
				$('#img-preview').hide();
				$('#preview-image').attr('src', ''); // 이미지 초기화
			}
		});

		$('#table-body').on('mouseover', 'tr', function () {
			if ($(this).attr('id') === 'table-tr' || $(this).attr('id') === 'preview-image' || $(this).attr('id') === 'img-preview') return;
			
			const imgFileName = $(this).data('image'); // `data-image` 속성에서 이미지 ID 가져오기
			const trOffset = $(this).offset();
			const trHeight = $(this).outerHeight();
			const windowHeight = $(window).height();
			const previewHeight = 200; // 이미지 높이 (예상)
			const trBottomPosition = trOffset.top + trHeight; // tr 아래쪽 위치
							
			if (imgFileName) {
				const imgSrc = `https://bims.sejong.go.kr/user-service/api/v1/images/editor/${imgFileName}`;
				// console.log('Generated imageURL: ', imgSrc);
				
				fetch(imgSrc)
					.then(response => {
						if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
						return response.blob();
					})
					.then(blob => {
						const objectURL = URL.createObjectURL(blob);
						//   console.log('Image loaded successfully:', objectURL);
						$('#preview-image').attr('src', objectURL);
				
						let newTop = trBottomPosition; // 기본적으로 tr 아래에 위치

						if ((newTop + previewHeight) > windowHeight) { // 이미지가 영역 벗어나는지 확인
							newTop = trOffset.top - previewHeight + 15; // 행 위로 이동
						}

						$('#img-preview').css({
							top: `${newTop}px`,  // 위치 조정
							left: `${trOffset.left + 180}px`,
							display: 'block'
						});
					})
					.catch(error => {
						console.error('Error fetching image:', error);
					});
			}
		});

		// `<tr>`에서 마우스가 벗어났을 때 이미지 숨기기
		$('#table-body').on('mouseleave', 'tr', function () {
			$('#img-preview').hide(); // 모달 숨기기
			$('#preview-image').attr('src', ''); // 이미지 소스 제거
		});
	}

	function bindPopupCleanupInterval() {
		setInterval(() => {
			for (const key in FacilityMng.popupRefs) {
				if (FacilityMng.popupRefs[key] && FacilityMng.popupRefs[key].closed) {
					FacilityMng.popupRefs[key] = null;
				}
			}
		}, 1000);
	}

	// 시설물 관리 초기화
	FacilityMng.initFacilityMng = function () {
		console.log("initFacilityMng");
		const userGroupId = $('#userGroupId').val() || "";
		const userRole = $('#userRole').val() || "";
		
		console.log("userGroupId : ", userGroupId, ": userRole : ", userRole);

		if (userGroupId == "" && userRole === "ADMIN") {
			$('#adMngBtn').css("visibility", "hidden");
		} else {
			$('#adMngBtn').css("visibility", "visible");
		}

		// 초기 데이터 검색
		FacilityMng.searchEroute?.();
		
		// 이벤트 바인딩
		bindEventListeners();
		bindMiscEventListeners();
		bindPopupCleanupInterval();

		$('#table-tr').css('display', 'table-row');
		$('#table-tr1').css('display', 'none');

		// viewType 선택 시 테이블 헤더 변경
		$('#viewType').change(function () {
			const isCard = $(this).val() === '1';
			$('#table-tr').toggle(!isCard);
			$('#table-tr1').toggle(isCard);
			FacilityMng.searchEroute();
		});

		$('#allEr').change(function () {
			$('#erCheckAll').prop('checked', false);
			if ($(this).prop('checked')) {
				let isRoadlineEmpty = $('#roadlineSearch').val().trim() === '';
				let isCarInfoEmpty = $('#carInfoSearch').val().trim() === '';
				let isErState = $('#erState').val() == '';
/*
				if (!isRoadlineEmpty || !isCarInfoEmpty || !isErState) {
					alert("노선, 차량, 상태 등 검색 항목에 내용이 있습니다. 확인하고 지워주세요.");
					$(this).prop('checked', false);
					return;
				}
*/					
				FacilityMng.tempErViewType = 1;
			} else {
				FacilityMng.tempErViewType = 0;
			}
			FacilityMng.searchEroute();
		});

		// 클릭된 링크를 저장할 LocalStorage 키 설정
		const visitedLinks = new Set(JSON.parse(localStorage.getItem('visitedLinks')) || []);

		// 저장된 방문한 링크를 찾아서 보라색으로 변경
		$('#table-body a').each(function () {
			if (visitedLinks.has($(this).attr('href'))) {
				$(this).css('color', 'purple');
			}
		});

		// 링크 클릭 시 방문한 것으로 저장
		$('#table-body').on('click', 'a', function () {
			const href = $(this).attr('href');
			if (href) {
				visitedLinks.add(href);
				localStorage.setItem('visitedLinks', JSON.stringify([...visitedLinks]));
				$(this).css('color', 'purple');
			}
		});

		$('#roadlineSearch, #carInfoSearch').on('focus input', function () {
			const isRoad = this.id === 'roadlineSearch';
			const query = $(this).val().toLowerCase();
			const $drop = isRoad ? $('#roadline') : $('#carInfo'); 
			
			$drop.find('li').each(function () {
				$(this).toggle($(this).text().toLowerCase().includes(query));
			});
			$drop.show();
		});

		// 외부 클릭 시 드롭다운 숨기기
		$(document).on('click', function (event) {
			if (!$(event.target).closest('#roadlineSearch, #roadline').length) {
				$('#roadline').hide();
			}
			if (!$(event.target).closest('#carInfoSearch, #carInfo').length) {
				$('#carInfo').hide();
			}
		});
		
		$('#roadline').on('click', 'li', function () {
			const routeId = $(this).data('routeid');
			const routeVer = $(this).data('routever');
			const isAllRouteline = (routeId === 0) ? true : false;

			console.log('Clicked routeId:', routeId, 'routeVer:', routeVer, 'isAllRouteline:', isAllRouteline);

			$('#roadlineSearch')
				.val($(this).text())
				.data('routeid', routeId)
				.data('routever', routeVer);
			
			$('#roadline').hide();
			$('#carInfoSearch').val('');
			
			const url = isAllRouteline ? '/facilityMng/updateAllBusList' : '/facilityMng/updateBusListInRoute';
			console.log("## facilitymng roadline(url) : ", url);
			const requestDAta = isAllRouteline ? { userGroupId }
				: {
					selRouteId: routeId,
					selRouteVer: routeVer,
					userGroupId: userGroupId,
				};
			
			$.ajax({
				url,
				type: 'GET',
				data: requestDAta,
				success: function (data) {
					updateCarInfo(data, isAllRouteline);
				}, 
				error: function (xhr, status, error) {
					console.error('Error fetching all buses:' , xhr, status, error);
				}
			});
		});

//		function updateCarInfo(response, isAllRouteline) {
		function updateCarInfo(response) {
       		$('#carInfo').empty();
               
            const data = response?.data || [];
        	console.log('updateCarInfo data : ', response);
        	console.log('data: ', data);
           
            if (data.length === 0) {
                $('#carInfo').append('<li data-vehid="">검색 결과 없음</li>');
                return;
            }
            
            $('#carInfo').append(`<li data-vehid="" data-routever="">해당노선버스전체</li>`);
            
            data.forEach(item => {         
            	$('#carInfo').append(
                	`<li data-vehid="${item.vehId}" data-routever="${item.routeVer}">${item.vehNo}</li>`
            	);
          	});
			
            $('#carInfo').show();
        }

         // 차량 선택 시
        $('#carInfo').on('click', 'li', function () {
        	const vehId = $(this).data('vehid');
           	$('#carInfoSearch')
           		.val($(this).text())
           		.data('vehid', vehId);
           	$('#carInfo').hide();
        });
        
        $('#searchBtn').click(function (e) {
			e.preventDefault();
			FacilityMng.tempErViewType = 0;
			FacilityMng.searchEroute();
        });

		FacilityMng.createRoadItemHTML = function (item, index) {
			console.log("create html");
			return `
				<div class="road_item1">
					<label class="ckbox_area">
					<div class="area_div">
						<input type="checkbox" class="selErId" data-vehno="${item.vehNo}" 
							data-routenm="${item.routeNm}" data-routedc="${item.routeDc}" 
							data-statcd="${item.statCd}" data-attchment="${item.attchment}" 
							value="${item.facilityId}">
						<span>
							<a href="#" class="erFormLink" data-facilityid="${item.facilityId}">
								${item.vehNo} / ${item.routeNm} (${item.routeDc}) - ${getStatusText(item.statCd)}
							</a>
							<br><br>
							<font color="black" class='span2'>${item.facilityId}&nbsp;/&nbsp;IP: ${item.ip}</font>
						</span>
						<img id="er_imgRoad_${index}" style="display:none;">
					</div>
					</label>
				</div>`;
		}
		
		bindPopupCleanupInterval();
	};

	FacilityMng.openErForm = function (facilityId) {
        if ($('.selErId:checked').length > 0) {
           alert('체크된 항목을 해제하세요.');
           return;
        }

		const userGroupId = document.getElementById('userGroupId')?.value || "";
		const popup = FacilityMng.openPopupOnce('erForm', 'FacilityInfo', '', 'width=704,height=550');
		if (!popup) return;

		const form = document.createElement('form');
		form.method = 'POST';
		form.action = '/facilityMng/er';
		form.target = 'FacilityInfo';
		form.style.display = 'none';

		const input1 = document.createElement('input');
		input1.type = 'hidden';
		input1.name = 'userGroupId';
		input1.value = userGroupId;
		form.appendChild(input1);

		const input2 = document.createElement('input');
		input2.type = 'hidden';
		input2.name = 'facilityId';
		input2.value = facilityId;
		form.appendChild(input2);

		document.body.appendChild(form);
		form.submit();
		document.body.removeChild(form);
	};

	FacilityMng.openAdMngPopup = function () {
		const selErs = [];
		document.querySelectorAll('.selErId:checked').forEach(chk => {
			selErs.push({
				facilityId: chk.value,
				erVehno: chk.dataset.vehno,
				erRoutenm: chk.dataset.routenm,
				erRouteDc: chk.dataset.routedc
			});
		});

		if (selErs.length < 1) {
			alert('하나 이상의 항목을 선택하세요.');
			return;
		}
		
		const userGroupId = document.getElementById("userGroupId")?.value || "";
		const popup = FacilityMng.openPopupOnce('adMng', 'ermngBypr', '', 'width=1400,height=970');
		if (!popup) return;

		const form = document.createElement('form');
		form.method = 'POST';
		form.action = '/facilityMng/ermngByPr'; // 컨트롤러 URL
		form.target = 'ermngByPr'; // 팝업 창 이름
		form.style.display = 'none';

		// hidden input 추가 : selErs
		const input1 = document.createElement('input');
		input1.type = 'hidden';
		input1.name = 'selErs';
		input1.value = JSON.stringify(selErs);
		form.appendChild(input1);

		// hidden input 추가 : userGroupId
		const input2 = document.createElement('input');
		input2.type = 'hidden';
		input2.name = 'userGroupId';
		input2.value = userGroupId;
		form.appendChild(input2);
		
		document.body.appendChild(form);
		form.submit(); // 폼 제출 (POST 방식)
		document.body.removeChild(form);
	}; 

	FacilityMng.openCntrPopup = function () {
		const selErs = [];
		document.querySelectorAll('.selErId:checked').forEach(chk => {
			selErs.push({
				facilityId: chk.value,
				vehNo: chk.dataset.vehno,
				routeNm: chk.dataset.routenm,
				routeDc: chk.dataset.routedc
			});
		});

		if (selErs.length < 1) {
			alert('하나 이상의 항목을 선택하세요.');
			return;
		}
           
           	const popup = FacilityMng.openPopupOnce('erCntr', 'erouteCntr', '', 'width=700, height=600');
			if (!popup) return;

			const form = document.createElement('form');
	        form.method = 'POST';
	        form.action = '/facilityMng/erCntr/cntr';  // 컨트롤러 URL
	        form.target = 'erouteCntr'; // 팝업 창 이름
	        form.style.display = 'none';
	
	        // hidden input 추가
	        const input = document.createElement('input');
	        input.type = 'hidden';
	        input.name = 'selErs';
	        input.value = JSON.stringify(selErs);
	        form.appendChild(input);

	        document.body.appendChild(form);
	        form.submit(); // 폼 제출 (POST 방식)
	    	document.body.removeChild(form);
	};

	FacilityMng.openMaPopup = function () {
		const selCheckItem = document.querySelector('#table-body input.selErId:checked');
		if (!selCheckItem) {
			alert('하나의 항목을 선택하세요.');
			return;
		}
		
		const selErData = {
			vehNo: selCheckItem.dataset.vehno,
			routeNm: selCheckItem.dataset.routenm,
			routeDc: selCheckItem.dataset.routedc,
			stateCd: selCheckItem.dataset.statcd,
			attchment: selCheckItem.dataset.attchment,
			facilityId: selCheckItem.value
		};

		const popup = FacilityMng.openPopupOnce('erMa', 'erouteMa', '', 'width=1020,height=970');
		if (!popup) return;

		const form = document.createElement('form');
		form.method = 'POST';
		form.action = '/facilityMng/erouteMa/maPg';
		form.target = 'erouteMa';
		form.style.display = 'none';

		const input = document.createElement('input');
		input.type = 'hidden';
		input.name = 'selErData';
		input.value = JSON.stringify(selErData);
		form.appendChild(input);

		document.body.appendChild(form);
		form.submit();
		document.body.removeChild(form);
	};

	function bindEventListeners() {
		document.addEventListener('click', function (e) {
			const { target } = e;

			if (target.classList.contains('erFormLink')) {
				const facilityId = target.dataset.facilityid;
				FacilityMng.openErForm(facilityId);
			}

			switch (target.id) {
				case 'adMngBtn':
					FacilityMng.openAdMngPopup?.();
					break;
				case 'mainmngBtn':
					FacilityMng.openMaPopup?.();
					break;
				case 'statCntrBtn':
					FacilityMng.openCntrPopup?.();
					break;
				case 'erCheckAll':
					FacilityMng.toggleSelectAll(target);
					break;
			}
		});

		const facilityIdHeader = document.querySelector('th:nth-child(6)');
		if (facilityIdHeader) {
			facilityIdHeader.addEventListener('dblclick', () => {
				FacilityMng.toggleFacilityId();
			});
		}

		document.querySelectorAll('.clearBtn')?.forEach(btn => {
			btn.addEventListener('click', () => {
				const targetId = btn.dataset.target || btn.previousElementSibling?.id;
				if (targetId) FacilityMng.clearInput(targetId);
			});
		});
	}

	FacilityMng.searchEroute = function () {
		const selRouteId = $('#roadlineSearch').data('routeid') || 0;
		const selRouteVer = $('#roadlineSearch').data('routever') || 1;
		const selVehId = $('#carInfoSearch').data('vehid') || 0;
		const selState = $('#erState').val();
		const userGroupId = $('#userGroupId').val() || "";
		const viewTypeA = $('#viewType').val();

		// $('#table-body input[type="checkbox"]').prop('checked', false);
		$.ajax({
			url: '/facilityMng/searchEr',
			type: 'GET',
			data: {
				selRouteId,
				selRouteVer,
				selVehId,
				selState,
				viewType: FacilityMng.tempErViewType,
				userGroupId,
			},
			success: function (response) {
				const container = $('#table-body');
				container.empty();

				if (response && response.data.length > 0 && response.data !== null) {
					if (viewTypeA === '0'){
						response.data.forEach(function (item, index) {
							const roadItem = `
								<tr data-image="${item.attchment}" id="er_img_${index}">
								<td style="width: 3.2%;"><input type="checkbox" class="selErId" data-vehno="${item.vehNo}" 
											data-routenm="${item.routeNm}" data-routedc="${item.routeDc}" 
											data-statcd="${item.statCd}" data-attchment="${item.attchment}" 
											value="${item.facilityId}"> </td>
								<td style="width: 3.01%;">${index + 1}</td>
								<td><a href="#" class="erFormLink" data-facilityid="${item.facilityId}">${item.vehNo}</a></td>
								<td>${item.routeNm}</td>
								<td style="width: 30%;">${item.routeDc}</td>
								<td>${getStatusText(item.statCd)}</td>
								<td class="hidden">${item.facilityId}</td>
								<td class="hidden">${item.ip}</td>
								<td>${item.lastUpdtDt}</td>
								</tr>
								`;
							container.append(roadItem);
						});
					} else if (viewTypeA === '1') {
						let rowHTML = '';
						let imageLoadQueue = []; // 이미지 로드할 배열

						for (let i = 0; i < response.data.length; i += 2) {
							let item1 = response.data[i];
							let item2 = response.data[i + 1] || null;

							rowHTML += `<tr>`;
							rowHTML += `<td style="width: 50%; padding: 5px;">${FacilityMng.createRoadItemHTML(item1, i)}</td>`;
							
							if (item2) {
								rowHTML += `<td style="width: 50%;">${FacilityMng.createRoadItemHTML(item2, i + 1)}</td>`;
							} else {
								rowHTML += `<td style="width: 50%;"></td>`;
							}

							rowHTML += `</tr>`;
							
							// 이미지 로드 대기 목록 추가
							imageLoadQueue.push({ attchment: item1.attchment, id: `er_imgRoad_${i}` });
							if (item2) {
								imageLoadQueue.push({ attchment: item2.attchment, id: `er_imgRoad_${i + 1}` });
							}
						}
						container.append(rowHTML);
						setTimeout(() => {
							imageLoadQueue.forEach(item => {
								if (item.attchment) {
								loadFile(item.attchment, item.id);
								}
							});
						}, 500);
					}
					if (FacilityMng.isFacilityHidden) {
						$('th:nth-child(7), td:nth-child(7), th:nth-child(8), td:nth-child(8)').addClass('hidden');
					} else {
						$('th:nth-child(7), td:nth-child(7), th:nth-child(8), td:nth-child(8)').removeClass('hidden');
					}

				} else {
					container.append('<tr><td colspan="6" style="background-color:#FFFFFF;"><font color="#00008b"><span>검색된 시설물이 없습니다.</span></font></td></tr>');
				}
			},
			error: function (xhr, status, error) {
				console.log('Ajax data : ', {
				strRouteId: selRouteId,
				strRouteVer: selRouteVer,
				strVehId: selVehId,
				strState: selState,
				viewType: FacilityMng.tempErViewType,
				});
				console.error('AJAX error : ', xhr, status, error);
			}
		}); 
	}

	window.FacilityMng = FacilityMng;
     
     setTimeout(() => {
		FacilityMng.initFacilityMng();
	 }, 0);
})();