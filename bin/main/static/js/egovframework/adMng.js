// adMng.js

		    function openAdPopup(adId){
			var url = adId ? './ad/' + adId : './ad/new';
	        var popup = window.open(url, '홍보 상세 정보', 'width=750,height=730');
			if (!popup) {
			    alert('팝업을 열 수 없습니다. 팝업 차단을 확인해주세요.');
			}
	    }
		
		function openAdAdminPopup(){
			const userId = document.getElementById('userId').value;
			var url = './adminAd/'+userId;
			var popup = window.open(url, '홍보 상세 정보(admin)', 'width=1400,height=800');
			if (!popup) {
			    alert('팝업을 열 수 없습니다. 팝업 차단을 확인해주세요.');
			}
		}
		
		function delAdFun() {
			//선택된 체크박스에서 홍보 ID 수집
			const selectedAds = Array.from(document.querySelectorAll('input[name="selAdIds"]:checked')).map(checkbox => {
				return {
					adId: checkbox.value,
					attchment: checkbox.dataset.attchment
				};
			});
		
			//삭제할 홍보물이 선택되지 않은 경우 경고 메시지
			if (selectedAds.length === 0) {
				alert('삭제할 홍보를 선택하세요.');
				return;
			}
			
			//삭제 확인 메시지
			const confirmDelete = confirm('선택된 홍보를 삭제하시겠습니까? 삭제를 선택한다면 \n1. 홍보 리스트에서 삭제, \n2. 시설물에 등록한 홍보 리스트에서 삭제, \n3. 첨부파일이 있다면 첨부파일 삭제 모두 진행합니다. \n정말로 삭제하시겠습니까?');
			
			if (confirmDelete) {
				$.ajax({
					url: './deleteAds',
					type: 'POST',
					contentType: 'application/json',
					data: JSON.stringify(selectedAds),
					success: function(response) {
						alert('홍보를 성공적으로 삭제하였습니다.');
						location.reload();
					},
					error: function(xhr, status, error) {
						console.error('오류 발생 : ', error);
						alert('홍보 삭제 중 오류가 발생했습니다.');
					}
				});
			}
		}
		
		function toggleselAll(selectAllckbox) {
			const adCkboxes = document.querySelectorAll('input[name="selAdIds"]');
			adCkboxes.forEach((checkbox) => {
				checkbox.checked = selectAllckbox.checked;
			});
		}
		
		document.addEventListener('DOMContentLoaded', function() {
			console.log("aaaaaaaaaaaaaaaaa");
			const userGroupId = document.getElementById('userGroupId')?.value || "";
			const userRole = document.getElementById('userRole')?.value || "";
			console.log("#@ userGroupId : ", userGroupId);
			console.log("@# userRole: ", userRole) 
/*
			if (userId == "" && userRole == "100000000"){
				$('#add_ad_btn').hide();
				$('#add_ad_btn_admin').hide();
				$('#del_ad_btn').hide();
			} else if (userId != "" && userId != groupId) {
				$('#add_ad_btn').hide();
				$('#add_ad_btn_admin').show();
				$('#del_ad_btn').hide();
			} else {
				$('#add_ad_btn').show();
				$('#add_ad_btn_admin').hide();
				$('#del_ad_btn').show();
			}
*/

			if (userGroupId === "" && userRole === "ADMIN"){
				$('#add_ad_btn').css("visibility", "hidden");
				$('#del_ad_btn').css("visibility", "hidden");
				$('#add_ad_btn_admin').css("visibility", "hidden");
			} else if (userGroupId != "" && userRole === 'ADMIN') {
				$('#add_ad_btn').css("visibility", "hidden");
				$('#del_ad_btn').css("visibility", "hidden");
				$('#add_ad_btn_admin').css("visibility", "visible");
			} else {
				$('#add_ad_btn').css("visibility", "visible");
				$('#del_ad_btn').css("visibility", "visible");
				$('#add_ad_btn_admin').css("visibility", "hidden");
			}
		
			// 빈 곳 채울 때 column 개수 확인
			ensureFixRows('#results', 6, 15);
		    updatePagination(totalPg, curPg, 'adAllList', 'loadAds');

		    $(`.adAllList .page-link`).on('click', function (e) {
		        e.preventDefault();
		        const page = $(this).data('page');
				
				if( page !== undefined) {
					loadAds(page);
				} else {
					alert('현재 페이지입니다.');
				}
		    });
			
			//검색 버튼 클릭시 페이지를 1로 설정
			document.getElementById('searchBtn').addEventListener('click', function() {
				document.getElementById('curPg').value = 1;
			});
		});

		function loadAds(page) {
			document.getElementById('curPg').value = page;
		    document.getElementById('searchForm').submit();
		}	
