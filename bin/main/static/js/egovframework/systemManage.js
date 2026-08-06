//------------------------------------------------------------------------------------------------
const fileInput = document.getElementById('file');
const fileNameDisplay = document.getElementById('fPath');

fileInput.addEventListener('change', function() {
    const fileName = fileInput.files.length > 0 ? fileInput.files[0].name : "선택된 파일 없음";
    fileNameDisplay.textContent = "선택한 파일: " + fileName;
});