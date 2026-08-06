document.addEventListener('DOMContentLoaded', function () {
    flatpickr.localize(flatpickr.l10ns.ko);
    
    const installDeInput = document.getElementById('installDe');
    const today = new Date().toISOString().split('T')[0];

    if (installDeInput) {
        const button = document.createElement('img');
        button.src = '/images/egovframework/calendar.png';
        button.alt = '날짜 선택';
        button.style.width = '28px';
        button.style.cursor = 'pointer';
        button.style.position = 'absolute';
        button.style.top = '25%';
        button.style.right = '3rem';

        button.addEventListener('click', () => {
            installDeInput._flatpickr.open(); // flatpickr 열기
        });

        const parent = installDeInput.parentNode;
        parent.style.position = 'relative';
        parent.appendChild(button);

        // flatpickr 초기화
        flatpickr(installDeInput, {
            dateFormat: "Y-m-d", // 날짜 포맷
            defaultDate: (installDeInput.value !== null && installDeInput.value !== "") ? installDeInput.value : today,
            locale: "ko",
        });

        // 기본 날짜 설정 (오늘 날짜로)
        if (installDeInput && !installDeInput.value) {
            installDeInput.value = today;
        }
    }
});