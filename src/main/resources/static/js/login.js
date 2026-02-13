// フォーム要素を取得
const form = document.getElementById('login-form');

// submitイベントをリッスン
form.addEventListener('submit', function(event) {
    // 通常のフォーム送信（ページ遷移）をキャンセル
    event.preventDefault();

    // 入力値の取得
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    fetchWithAuth(null, '/api/auth/login', 'POST', { email: email, password: password})
    .then(data => {
        // トークンをlocalStorageに保存
        localStorage.setItem('accessToken', data.accessToken);

        // 別ページへ移動
        window.location.href = '/index.html';
    })
    .catch(error => {
        // エラーメッセージを画面に表示
        const errorDiv = document.getElementById('error-message');
        errorDiv.textContent = 'ログインに失敗しました。メールアドレスまたはパスワードを確認してください。';
        errorDiv.hidden = false;
    });
});
