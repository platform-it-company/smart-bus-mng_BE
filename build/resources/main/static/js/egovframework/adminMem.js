let page = 1;
const size = 3;
let isLoading = false;

function loadMoreMembers() {
  if (isLoading) return;
  isLoading = true;

  $.ajax({
    url: `/erouteapi/members?page=${page}&size=${size}`,
    type: "GET",
    success: function (data) {
      if (data.length === 0) {
        // 더 이상 없음
        $('#tableWrapper').off('scroll');
        return;
      }

      data.forEach((member, index) => {
        const row = `
          <tr>
            <td><input type="checkbox" /></td>
            <td>${(page - 1) * size + index + 1}</td>
            <td>${member.name}</td>
            <td>${member.group}</td>
            <td>${member.email}</td>
            <td>${member.tel}</td>
            <td>${member.registeredAt}</td>
          </tr>
        `;
        $('#memberListBody').append(row);
      });

      page++;
      isLoading = false;
    },
    error: function () {
      console.error('불러오기 실패');
      isLoading = false;
    }
  });
}

// 처음 1페이지 불러오기
loadMoreMembers();

// 스크롤 이벤트 바인딩
$('#tableWrapper').on('scroll', function () {
  const wrapper = $(this);
  if (wrapper.scrollTop() + wrapper.innerHeight() >= wrapper[0].scrollHeight - 10) {
    loadMoreMembers();
  }
});
