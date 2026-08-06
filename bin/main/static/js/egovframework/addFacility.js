
export function initAddFacilityJs() {
    initDatePicker();
    initUnitPrice();
    registerSubmitHandler();
}

function initDatePicker() {
    flatpickr.localize(flatpickr.l10ns.ko);
    const dateInputs = document.querySelectorAll('input[type="text"]#installDe');
    const today = new Date().toISOString().split('T')[0];

    dateInputs.forEach(dateInput => {
        // flatpickr 먼저 초기화
        const fp = flatpickr(dateInput, {
            dateFormat: "Y-m-d",
            defaultDate: new Date(),
            locale: "ko"
        });
        
        // Datepicker 버튼 추가
        button.src = '/images/egovframework/calendar.png';
        button.alt = 'Date Picker';
        button.style.width = '28px';
        button.style.cursor = 'pointer';
        button.style.position = 'absolute';
        button.style.top = '25%';
        button.style.right = '5rem';
        button.addEventListener('click', () => {
            fp.open(); // flatpickr의 open() 메서드 호출
        });

        // input의 부모에 버튼 추가
        const parent = dateInput.parentNode;
        parent.style.position = 'relative';
        parent.appendChild(button);
        input.value = today;
    });
}

function initUnitPrice() {
    const unitPriceInput = document.getElementById("unitPrice");
    if (!unitPriceInput) return;

    // console.log('installDe type : ', document.getElementById('installDe').type);
    // 입력값 변경될 때 쉼표 추가
    unitPriceInput.addEventListener("input", function (event) {
        let value = event.target.value.replace(/,/g, ""); // 기존 쉼표 제거
        if (!isNaN(value) && value !== "") {
            event.target.value = Number(value).toLocaleString(); // 1000 단위 쉼표 추가
        } else {
            event.target.value = ""; // 숫자가 아니면 비움
        }
    });
}

function registerSubmitHandler() {
    const form = document.getElementById("erouteForm");
    if (form) {
        form.addEventListener("submit", submitEr);
    }
}
function submitEr(event) {
    event.preventDefault();

    const form = document.getElementById('erouteForm');
    const actURL = form.getAttribute('action');
    const formData = new FormData(form);

    // 검증 로직 추가
    const displayRes = document.getElementById('displayRes').value;
    const routeMapRes = document.getElementById('routeMapRes').value;
    const adAreaRes = document.getElementById('adAreaRes').value;
    const vehId = document.getElementById('vehId').value;
    let modelNm = document.getElementById('modelNm').value;
    const otherModelNm = document.getElementById('otherModelNm').value;
    let manufacturer = document.getElementById('manufacturer').value;
    const otherManufacturer = document.getElementById('otherManufacturer').value;
    const installDe = document.getElementById('installDe').value;
    const unitPrice = document.getElementById('unitPrice').value.replace(/,/g, ""); // 콤마 제거

    if (modelNm === 'other' && !otherModelNm) {
        alert("모두 필수항목입니다. 모델명을 확인하세요.");
        return;
    }
    
    if (modelNm === 'other' && otherModelNm) {
        formData.set('modelNm', otherModelNm);
    }
    
    if (manufacturer === 'other' && !otherManufacturer) {
        alert("모두 필수항목입니다. 제조회사를 확인하세요.");	
        return;
    }     
    if (manufacturer === 'other' && otherManufacturer) {
        formData.set('manufacturer', otherManufacturer);
    }				

    if (!installDe) {
        alert("모두 필수항목입니다. 설치 날짜를 입력하세요.");
        return;
    }

    if (!displayRes){
        alert("모두 필수항목입니다. 디스플레이 해상도를 확인하세요.");
        return;				
    }
    
    if(!routeMapRes){
        alert("모두 필수항목입니다. 전자노선도 해상도를 확인하세요.");
        return;
    } 
    
    if(!adAreaRes) {
        alert("모두 필수항목입니다. 광고영역 해상도를 확인하세요.");
        return;				
    } 
                        
    if(!vehId ) {
        alert("모두 필수항목입니다. 설치 차량을 확인하세요.");
        return false;				
    }
    
    if (!unitPrice) {
        alert("모두 필수항목입니다. 도입 단가를 입력하세요.");
        return;
    }

    if (isNaN(unitPrice) || !Number.isInteger(Number(unitPrice))) {
        alert("도입단가는 정수만 입력 가능합니다.");
        return;
    }

    fetch(actURL, {
        method: 'POST',
        body: formData
    }).then(response => {
        if (response.ok) {
            return response.json().catch(() => {
                throw new Error('서버에서 유효한 JSON 응답을 받지 못했습니다.');
            });
        } else {
            return response.json().then(err => {
                throw new Error('서버 오류: ' + (err.message || response.statusText));
            });
        }
    }).then(data => {
        if (data.status === 'success') {
            Swal.fire({
                title: '시설물을 추가하였습니다.',
                imageUrl: '/images/egovframework/submit.png',
                imageWidth: 290,
                imageHeight: 300,
                imageAlt: 'submit'
            }).then(() => {
                window.location.reload();
            });
        } else {
            alert("저장 실패: " + data.message);
        }
    }).catch(error => {
        alert("오류 발생: " + error.message);
    });
}

export function resetForm() {
    document.getElementById("erouteForm").reset();

    // modelNm 초기화 (기존 <select> 유지)
    const modelNm = document.getElementById("modelNm");
    const otherModelNm = document.getElementById("otherModelNm");
    modelNm.style.display = "inline"; // select 표시
    modelNm.value = modelNm.options[0].value; // 첫 번째 옵션 선택 (Plat-U43-ST 등)
    otherModelNm.style.display = "none"; // 기타 입력칸 숨김
    otherModelNm.value = "";

    // manufacturer 초기화 (기존 <select> 유지)
    const manufacturer = document.getElementById("manufacturer");
    const otherManufacturer = document.getElementById("otherManufacturer");
    manufacturer.style.display = "inline";
    manufacturer.value = manufacturer.options[0].value;
    otherManufacturer.style.display = "none";
    otherManufacturer.value = "";

    const unitPriceInput = document.getElementById("unitPrice");
    unitPriceInput.ariaPlaceholder = "금액을 입력하면 ,는 자동으로 입력됩니다.";
}

export function handleSelectChange(selectElement, otherElementId) {
    const otherInput = document.getElementById(otherElementId);
    if (selectElement.value === 'other') {
        otherInput.style.display = 'inline';
        otherInput.value = '';                
        selectElement.style.display = 'none';
    } else {
        otherInput.style.display = 'none';
        otherInput.value = '';
        selectElement.style.display = 'inline';
    }
}

export function onManufacturerChange() {
	const selManufacturer = document.getElementById('manufacturer').value;
	
	if (selManufacturer === '__add__') {
		document.getElementById('newManufacturerInput').style.display = 'inline';
		document.getElementById('addMnaufacturer').style.display = 'inline';
		return;
	}
	
	document.getElementById('newManufacturerInput').style.display = none;
	document.getElementById('addManufacturerBtn').style.display = none;
	
	fetch(`/facilityMng/getModelList?manufacturer=${encodeURIComponent(selManufacturer)}`)
		.then(res => res.json())
		.then(models => {
			const selModel = document.getElementById('modelNm');
			selModel.innerHTML = `<option value="">모델 선택</option>`;
			models.forEach(model => {
				selModel.innerHtml += `<option value=${model}">${model}</option>`;
			});
			selModel.innerHTML += `<option value="__add__">+ 모델 추가</option>`;
		});
}