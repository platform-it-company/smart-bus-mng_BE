document.addEventListener('DOMContentLoaded', function () {
    // 모든 date input 선택
    flatpickr.localize(flatpickr.l10ns.ko);
    const dateInputs = document.querySelectorAll('.input_wrap input[type="text"]');
    const today = new Date().toISOString().split('T')[0];

    dateInputs.forEach(dateInput => {
        // Datepicker 버튼 추가
        const button = document.createElement('img');
        button.src = '../../images/calendar.png';
        button.alt = 'Date Picker';
        button.style.width = '28px';
        button.style.cursor = 'pointer';
        button.style.position = 'absolute';
        button.style.top = '25%';
        button.style.right = '3rem';

        // button 클릭 시 날짜 선택기 열기
        button.addEventListener('click', () => {
            dateInput._flatpickr.open(); // flatpickr의 open() 메서드 호출
        });

        // input의 부모에 버튼 추가
        const parent = dateInput.parentNode;
        parent.style.position = 'relative';
        parent.appendChild(button);

        // flatpickr 초기화
        const fp = flatpickr(dateInput, {
            dateFormat: "Y-m-d", // 날짜 포맷
            defaultDate: (dateInput.value !== null && dateInput.value !== "") ? dateInput.value : today,
            local: "ko",
        });
    });

    // // 오늘 날짜로 기본 값 설정 달력에 표시함
    
    // dateInputs.forEach(input => {
    //     input.value = (input.value !== null) ? input.value : today;
    // });
});