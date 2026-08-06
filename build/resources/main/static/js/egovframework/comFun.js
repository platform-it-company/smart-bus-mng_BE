function ensureFixRows(selector, colmnCnt, pgSize) {
	const rows = document.querySelectorAll(selector+' tbody tr');
	const numRows = rows.length;
	for(let i= numRows; i < pgSize; i++) {
		const emptyRow = document.createElement('tr');
		emptyRow.innerHTML = `<td colspan="${colmnCnt}" style="background-color: white; border : 0px;">&nbsp;</td>`;
		document.querySelector(selector + ' tbody').appendChild(emptyRow);
	}			
}

function updatePagination(totalPg, curPg, pgContainer, loadFunc) {				
    const pagination = $(`.${pgContainer}`);
    pagination.empty();

    // 페이지 범위 계산
    let startPg = Math.max(1, curPg - 2);
    let endPg = Math.min(totalPg, curPg + 2);

    if (curPg > 1) {

        pagination.append(`
            <a class="page-link pagination-first ${curPg === 1 ? 'disabled' : ''}" 
            href="#" 
            data-page="1" 
            data-loadfunc="${loadFunc}">
            <<
            </a>
        `);

        pagination.append(`
            <a class="page-link pagination-newer ${curPg === 1 ? 'disabled' : ''}" 
            href="#" 
            data-page="${curPg > 1 ? curPg - 1 : ''}" 
            data-loadfunc="${loadFunc}">
            Prev
            </a>
        `);

    }

    // 표시할 페이지가 5개 미만일 경우, 범위 조정
    if (endPg - startPg < 4) {
        if (curPg <= 3) {
            endPg = Math.min(5, totalPg);
        } else if (curPg >= totalPg - 2) {
            startPg = Math.max(1, totalPg - 4);
        }
    }

    // 페이지 번호 생성
    for (let i = startPg; i <= endPg; i++) {
        if (i === curPg) {
            pagination.append(`<span class="page-link pagination-inner active">${i}</span>`);
        } else {
            pagination.append(`<a class="page-link pagination-inner" href="#" data-page="${i}" data-loadfunc="${loadFunc}">${i}</a>`);
        }
    }

    if (curPg < totalPg) {
        // Next 버튼
        pagination.append(`
            <a class="page-link pagination-older ${curPg === totalPg ? 'disabled' : ''}" 
            href="#" 
            data-page="${curPg < totalPg ? curPg + 1 : ''}" 
            data-loadfunc="${loadFunc}">
            Next
            </a>
        `);
    // }
        // >> 버튼 (끝으로 이동)
    // if (endPg < totalPg) {
        pagination.append(`
            <a class="page-link pagination-last ${curPg === totalPg ? 'disabled' : ''}" 
            href="#" 
            data-page="${totalPg}" 
            data-loadfunc="${loadFunc}">
            >>
            </a>
        `);
    }
}

