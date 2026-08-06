const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');

signUpButton.addEventListener('click', () => {
  container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
  container.classList.remove("right-panel-active");
});

/**
 * 로그인 파라미터 에러 감지(sessionCheck 등)
 */
document.addEventListener('DOMContentLoaded', () => {
	const params = new URLSearchParams(window.location.search);
	const error = params.get("error");

	if (error === "bad_credentials") {
		Swal.fire({
			icon: 'error',
			title: '로그인 실패',
			text: '비밀번호가 일치하지 않습니다.',
		});
	} else if (error === "user_not_found") {
		Swal.fire({
			icon: 'error',
			title: '로그인 실패',
			text: '존재하지 않는 사용자입니다.',
		});
	} else if (error === "user_not_approved") {
		Swal.fire({
			icon: 'warning',
			title: '승인 대기',
			text: '아직 관리자의 승인을 받지 않았습니다.',
		});
	} else if (error === "sessionCheck") {
		Swal.fire({
			icon: 'warning',
			title: '세션 만료',
			text: '세션이 만료되었습니다. 다시 로그인해 주세요.',
		});
	} else if (error) {
		Swal.fire({
			icon: 'info',
			title: '알림',
			text: '알 수 없는 오류가 발생했습니다.',
		});
	}
});