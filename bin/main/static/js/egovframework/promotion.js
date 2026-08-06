//----------------------------------------------------------------------------------------------
// 스크롤 위치에 따라 버튼을 표시하거나 숨김
window.onscroll = function() {
  const scrollToTopBtn = document.getElementById("scrollToTopBtn");

  if (document.body.scrollTop > 100 || document.documentElement.scrollTop > 100) {
      // 스크롤이 100px 이상 내려갔을 때 버튼 표시
      scrollToTopBtn.style.display = "block";
  } else {
      // 스크롤이 100px 미만일 때 버튼 숨김
      scrollToTopBtn.style.display = "none";
  }
};

// 버튼 클릭 시 스크롤을 맨 위로 이동
document.getElementById("scrollToTopBtn").onclick = function() {
  window.scrollTo({
      top: 0,
      behavior: "smooth" // 부드럽게 스크롤
  });
};
