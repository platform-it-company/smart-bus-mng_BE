// DOMContentLoaded에서 초기화
document.addEventListener('DOMContentLoaded', function () {
  // 드롭다운 초기
	function setDropList(inputId, listId) {
		const inputItem = document.getElementById(inputId);
		const listItem = document.getElementById(listId);
		
	    inputItem.addEventListener('click', function() {
			listItem.style.display = 'block';
			inputItem.style.backgroundColor = 'lightblue';
		  	listItem.style.top = inputItem.offsetTop + inputItem.offsetHeight + 'px';
			listItem.style.left = inputItem.offsetLeft + 'px';	// 현재 입력된 값에 일치하는 항목으로 스크롤
	    	const query = inputItem.value.toLowerCase();
	        const listItems = Array.from(listItem.querySelectorAll('li'));
	        // 기존 배경 제거
	        listItems.forEach(item => item.style.backgroundColor = '');

	        const matchingItem = listItems.find(item => item.textContent.toLowerCase().includes(query));
		
	        if (matchingItem) {
	            matchingItem.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
	            matchingItem.style.backgroundColor = 'darkgray'; // 배경색 변경
	            matchingItem.style.transition = 'background-color 0.3s ease'; // 부드러운 전환 효과
	        }
		});

		inputItem.addEventListener("input", function () {
		    const query = inputItem.value.toLowerCase();
		    const listItems = listItem.querySelectorAll('li');
		    
		    if (query) {
				listItems.forEach(item => {
					item.style.display = item.textContent.toLowerCase().includes(query) ? '' : 'none';
				});
			}
		});

        // 드롭다운 항목 클릭 시 값 설정 및 드롭다운 숨기기
        listItem.addEventListener('click', function (e) {
            if (e.target.tagName === 'LI') {
            	const listItems = listItem.querySelectorAll('li');
            
	            // 기존 선택 스타일 초기화
	            listItems.forEach(item => item.style.backgroundColor = '');
	
	            // 선택된 항목 스타일 적용
	            e.target.style.backgroundColor = 'darkgray';				
                
            	inputItem.value = e.target.textContent;
                inputItem.dataset.value = e.target.getAttribute('data-routeid') || e.target.getAttribute('data-vehid');
	            listItem.style.display = 'none';
			}
        });

        // 드롭다운 외부 클릭 시 드롭다운 숨기기
        document.addEventListener('click', function (e) {
            if (!listItem.contains(e.target) && e.target !== inputItem) {
                listItem.style.display = 'none';
                inputItem.style.backgroundColor = 'white';
            }
        });		
	}
	
	setDropList('roadlineSearch', 'roadline');
	setDropList('carInfoSearch', 'carInfo');
});
//-------------------------------------------------------------------------------------------------------------
