// フォーム要素を取得
const form = document.getElementById('signup-form');

// submitイベントをリッスン
form.addEventListener('submit', function(event) {
    // 通常のフォーム送信（ページ遷移）をキャンセル
    event.preventDefault();

    // 入力値の取得
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const password_confirm = document.getElementById('password-confirm').value;

    if (password !== password_confirm) {
        // エラーメッセージを画面に表示
        const errorDiv = document.getElementById('error-message');
        errorDiv.textContent = 'パスワードとパスワード（確認用）が一致しません';
        errorDiv.hidden = false;
        return;  // ← ここで処理を終了
    }

    fetchWithAuth(null, '/api/auth/register', 'POST', { email: email, password: password})
    .then(data => {
        // 別ページへ移動
        window.location.href = '/login.html';
    })
    .catch(error => {
        // エラーメッセージを画面に表示
        const errorDiv = document.getElementById('error-message');
        errorDiv.textContent = 'ユーザー登録に失敗しました。メールアドレスまたはパスワードを確認してください。';
        errorDiv.hidden = false;
    });
});
