function getAuthToken() {
    const token = localStorage.getItem('accessToken');

    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    return token;
}

function fetchWithAuth(token, url, method, body) {
    let headers;
    if (!token) {
        headers = {
            'Content-Type': 'application/json'
        }
    } else {
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    }

    let options;
    if (method === "GET") {
        options = {
            method: method,
            headers: headers
        }
    } else {
        options = {
            method: method,
            headers: headers,
            body: JSON.stringify(body)
        }
    }

    console.log('fetchWithAuth called:', method, url);

    return fetch(url, options)
    .then(response => {
        if (response.status === 401) {
            window.location.href = '/login.html';
            return;
        } else if (response.status === 204) {
            return;
        } else {
            return response.json();
        }
    })
}
