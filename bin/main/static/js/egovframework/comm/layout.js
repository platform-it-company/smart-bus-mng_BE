/**
 * layout.js
 * 기능: session 관리, htmx를 통한 페이지별 JS/CSS 동적 로딩 및 동적 메뉴 구성
 */

const extendedTime = 1800; 	// 30*60 30min
let remainTime = 0;
let swalShown = false;
let timerId = null;			// timerId 저장

// ===== 세션 남은 시간 조회 및 타이머 시작 ===== 
function fetchRemainTimeAndStartTimer() {
	fetch('/auth/session/remain')
		.then(res => {
			if (res.redirected) {
				console.warn('세션 만료 감지 -> 로그인 페이지로 이동: ', res.url);
				window.location.href = res.url;
				return null;
			}
			if (!res.ok) throw new Error("서버 응답 오류");
			return res.json();
		})
		.then(data => {
			if (!data) return;
			console.log("remain sessiontime: ", data.remainSeconds);
			remainTime = data.remainSeconds;
			startTimer();
		})
		.catch(err => {
			console.error('세션 시간 조회 실패: ', err);
			if (window.self !== window.top) {
				window.top.location.href = '/login?error=sessionCheck';
			} else {
				window.location.href = '/login?error=sessionCheck';
			}
		});
}

// ===== 시간 포맷 helper =====
function formatTime(sec) {
	const min = Math.floor(sec/60);
	const secRemain = sec % 60;
	
	return `${min}:${secRemain.toString().padStart(2, '0')}`;
}

// ===== 세션 연장 알림 =====
function showExtendAlert() {
	if (Swal.isVisible()) return;
	
	Swal.fire({
		title: '세션 연장',
		text: '세션이 곧 만료됩니다. 연장하시겠습니까?', 
		icon: 'warning',
		showCancelButton: true,
		confirmButtonText: '연장 신청',
		cancelButtonText: '연장 취소'
	}).then((result) => {
		swalShown = false; 			// 알림 종료 후 항상 false로 초기화
		if (result.isConfirmed) {
			extendSession();
		}
	});
}

// ===== 세션 연장 요청 =====
function extendSession() {
	fetch('/auth/session/extend', {
		method: 'POST',
	})
	.then(res => {
		if (res.ok) {
			remainTime = extendedTime;
			Swal.fire('연장 완료', '세션이 30분 연장되었습니다.', 'success');
		} else {
			Swal.fire('실패', '세션 연장에 실패하였습니다.', 'error');
		}
	})
	.catch(() => {
		Swal.fire('오류', '서버와 통신에 실패했습니다.', 'error');
	})
	.finally(() => {
		swalShown = false; 
	});
}

// ===== 세션 시간 연장 ===== 
function updateSessionTimer() {
	const timerEl = document.getElementById("sessionTimer");
	if (!timerEl) return;
	
	if (remainTime <= 0) {
		timerEl.textContent = "세션이 만료되었습니다.";
		clearInterval(timerId);
		location.href = '/login?expired=true';
		return;
	}
	
	timerEl.textContent = `남은 시간: ${formatTime(remainTime)}`;
	
	if (remainTime <= 600 && !swalShown) {
		swalShown = true;
		showExtendAlert();
	}
	remainTime--;
}

// ===== 타이머 시작 =====
function startTimer() {
	if (timerId !== null) {
		clearInterval(timerId);
	}
	
	let elapsed = 0;
	
//	timerId = setInterval(updateSessionTimer, 1000);
	timerId = setInterval(() => {
		updateSessionTimer();
		
		elapsed++;
		if (elapsed % 60 === 0) {
			fetchRemainTimeAndStartTimer();
			elapsed = 0;
		}
	}, 1000);
	
 	updateSessionTimer();
}

// ===== 메뉴 동적 로딩 =====
function loadDynamicMenu() {
	fetch('/memberMng/getMenuList')
		.then(res => res.json())
		.then(menuList => {
			console.log("## menuList: ", menuList);

			const rootUl = document.getElementById('dynamicMenuList');
			if (!rootUl) return;
			rootUl.innerHTML = '';
			
			// 1. 메뉴를 map에 넣고 childrent 초기화
			const menuMap = new Map();
			menuList.forEach(menu => {
				menu.children = menu.children || [];
				menuMap.set(Number(menu.menuId), menu);
			});
			
			// 2. 부모 메뉴에 children 할당
			const topMenus = [];
			menuList.forEach(menu => {
				const parentId = menu.parentId;
				if (parentId === null || parentId === undefined) {
					topMenus.push(menu);
				} else {
					const parent = menuMap.get(Number(menu.parentId));
					if (parent) {
						parent.children.push(menu);
					}
				}
			});
			
			// 3. 렌더링
			topMenus.forEach(menu => {
				const li = document.createElement('li');
				li.classList.add('menu-item');
				
				const a = document.createElement('a');
				a.textContent = menu.menuNm;
				a.href = menu.url || 'javascript:void(0)';
				a.classList.add('menu-link');

				console.log("## a.href : ", menu.url);
				li.appendChild(a);
				
				let subUl = null;
				if (menu.children.length > 0) {
					subUl = document.createElement('ul');
					subUl.classList.add('sub-menu');
					subUl.style.display = 'none';
					
					menu.children.forEach(child => {
						const subLi = document.createElement('li');
						const subA = document.createElement('a');
						subA.textContent = child.menuNm;
						subA.href = child.url || child.menuPath;
						subA.classList.add('submenu-link');
						subA.addEventListener('click', (e) => {
							e.preventDefault();
							forceReloadWithTimestamp(subA.href, '#mainContent');
						});
						subLi.appendChild(subA);
						subUl.appendChild(subLi);
					});
					li.appendChild(subUl);
				} 
				
				a.addEventListener('click', (e) => {
	
					e.preventDefault();

					const hasChildren = menu.children.length > 0;
					const hasUrl = !!menu.url?.trim();
					const allSubMenus = document.querySelectorAll('#dynamicMenuList .sub-menu');

					// 1. url x , 하위 메뉴 o
					if (!hasUrl && hasChildren && subUl) {
						const isVisible = subUl.style.display === 'block';
						allSubMenus.forEach(el => {
							if (el !== subUl) el.style.display = 'none';
						});
						subUl.style.display = isVisible ? 'none' : 'block';
						return;
					}

					// 2. url o, 하위 메뉴 o
					if (hasUrl && hasChildren && subUl) {
						forceReloadWithTimestamp(menu.url, '#mainContent');
						const isVisible = subUl.style.display === 'block';
						allSubMenus.forEach(el => {
							if (el !== subUl) el.style.display = 'none';
						});
						subUl.style.display = isVisible ? 'none' : 'block';
						return;
					}

					// 3. url o, 하위 메뉴x
					if (hasUrl && !hasChildren) {
						const userRole = document.getElementById('userRole')?.value || '';
						if (userRole == "USER") {
							const userGroupId = document.getElementById('groupId')?.value || '';
							menu.url = menu.url+`?userGroupId=${userGroupId}`
						}
						forceReloadWithTimestamp(menu.url, '#mainContent');
					}
				});
				rootUl.appendChild(li);
			});
		})
		.catch(err => console.error("메뉴 로딩 실패:", err));
}

// ===== 공통 초기화 =====
document.addEventListener("DOMContentLoaded", function () {
	const userRole = document.getElementById('userRole').value;
	console.log("## role :", userRole);
	
	fetchRemainTimeAndStartTimer();
	loadDynamicMenu();
/*
	// ===== 메뉴 클릭시 하위 메뉴 토글(하나만 열리도록) =====
	document.getElementById('dynamicMenuList')?.addEventListener('click', function (e) {
		const clicked = e.target;
		
		// 상위 메뉴 클릭시에만 처리
		if (!clicked.classList.contains('menu-link')) return;
		
		e.preventDefault(0);
		
		const clickedLi = clicked.closest('li');
		const clickedSubUl = clickedLi?.querySelector('ul.sub-menu');
		
		// 1. 다른 하위 메뉴 닫기
		document.querySelectorAll('#dynamicMenuList ul.sub-menu').forEach(subUl => {
			if (subUl !== clickedSubUl) {
				subUl.style.display = 'none';
			}
		});
		
		// 2. 현재 클릭한 것만 토글
		if (clickedSubUl) {
			const isVisible = clickedSubUl.style.display === 'block';
			clickedSubUl.style.display = isVisible ? 'none' : 'block';
		}
		
		// 3. 페이지 로딩 실행
		if (!clickedSubUl && clicked.href && clicked.href !== 'javascript:void(0)') {
			forceReloadWithTimestamp(clicked.href, '#mainContent');
		}
	});
	*/
	
	document.body.addEventListener("htmx:afterSwap", function (evt) {
		if (!evt.detail.elt) return;
		
		const pageMeta = evt.detail.elt.querySelector("#pageMeta");
		if (!pageMeta) return;
		
		// 기존 페이지 CSS 제거
		document.querySelectorAll('link[data-page-css="true"]').forEach(link => link.remove());
		// 페이지별 CSS 동적 로드
		if (pageMeta && pageMeta.dataset.pageCss) {
			pageMeta.dataset.pageCss.split(',').forEach(cssPath => {
				const path = cssPath.trim();
				if (!document.querySelector(`link[href="${path}"]`)) {
					const link = document.createElement('link');
					link.rel = 'stylesheet';
					link.href = path;
					link.setAttribute('data-page-css', 'true');
					document.head.appendChild(link);					
				}
			});
		}
		
		// 기존 페이지 JS 제거
		document.querySelectorAll('script[data-page-js="true"]').forEach(script => script.remove());
		// 페이지별 JS 동적 로드
		if (pageMeta && pageMeta.dataset.pageJs) {
			pageMeta.dataset.pageJs.split(',').forEach(jsPath => {
				const path = jsPath.trim();
				if (!document.querySelector(`script[src="${path}"]`)) {
					const script = document.createElement('script');
					script.src = path;
					script.setAttribute('data-page-js', 'true');
					document.head.appendChild(script);					
				}
			});
		}
		
		// inline script 실행
		const inlineScripts = evt.detail.elt.querySelectorAll("script[data-inline='true']");
		inlineScripts.forEach(script => {
			const newScript = document.createElement('script');
			newScript.textContent = script.textContent;
			document.body.appendChild(newScript);
			script.remove();
		});
	});
	
	document.addEventListener('contextmenu', function (event) {
		event.preventDefault(); // 우클릭 방지
	 });
	
	document.addEventListener('keydown', function (event) {
	    if (event.ctrlKey && (event.key === 'c' || event.key === 'C')) {
	        event.preventDefault(); // Ctrl + C 방지
	    }
	});
	
	document.addEventListener('copy', (event) => {
	    event.preventDefault();
	    alert("텍스트 복사가 차단되었습니다.");
	});
	
	document.addEventListener('selectstart', function (event) {
	    event.preventDefault(); // 텍스트 선택 방지
	});
	
	function closeAndReload() {
	    if (window.opener != null && !window.opener.closed) {
	        window.opener.location.reload();
	    }
	    window.close();
	}
});