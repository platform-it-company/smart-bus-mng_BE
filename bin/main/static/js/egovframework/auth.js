async function apiFetch(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
//            "Authorization": accessToken ? `Bearer ${accessToken}` : "",
            ...options.headers
        }
    });

    if (!response.ok) {
        console.error("🚨 API 요청 실패:", response.status);
        if (response.status === 401) {
            alert("인증이 필요합니다. 로그인 페이지로 이동합니다.");
            window.location.href = "/";
        }
        throw new Error("API 요청 실패");
    }
    return response.json();
}

function getCookie(name) {
    const cookieStr = document.cookie.split("; ").find(row => row.startsWith(name + "="));
    return cookieStr ? cookieStr.split("=")[1] : null;
}

async function validateToken() {
    try {
		console.log("validate call");
        await apiFetch("/erouteapi/auth/validateToken");
        console.log("✅ 인증 확인 완료");
    } catch (error) {
        console.error("❌ 인증 실패", error);
    }
}