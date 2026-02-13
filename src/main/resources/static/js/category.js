// 認証チェック
const token = getAuthToken();

// 既存カテゴリー更新の場合、該当Idをここに格納
let editingCategoryId = null;

// カテゴリー一覧取得
fetchWithAuth(token, url='/api/categories', method='GET', body={})
.then(data => {
    // 既存行をクリア（再検索・再描画で重複しないように）
    const tbody = document.getElementById('category-list');
    tbody.innerHTML = '';

    data.forEach(category => {
        const tr = document.createElement('tr');

        // innerHTML で埋め込むとXSSの危険があるので、
        // 基本は textContent で安全にセットする

        const tdName = document.createElement('td');
        tdName.textContent = category.name ?? '';

        const tdColor = document.createElement('td');
        tdColor.textContent = category.color ?? '';

        const tdEdit = document.createElement('td');
        const editBtn = document.createElement('button');
        editBtn.type = 'button';
        editBtn.textContent = '編集';
        editBtn.addEventListener('click', function() {
            editingCategoryId = category.id;
            document.getElementById('name').value = category.name;
            document.getElementById('color').value = category.color;
        });
        tdEdit.appendChild(editBtn);

        const tdDelete = document.createElement('td');
        const deleteBtn = document.createElement('button');
        deleteBtn.type = 'button';
        deleteBtn.textContent = '削除';
        deleteBtn.addEventListener('click', function() {
            // 確認ダイアログ
            if (!confirm("削除してもよいですか？")) {
                return;
            }

            // fetchでDELETE APIを呼ぶ
            fetchWithAuth(
                token,
                '/api/categories/' + category.id,
                'DELETE',
                {}
            )
            .then(() => window.location.reload()) // 成功したら一覧を再読み込み
            .catch(err => {
                console.error('Delete failed:', err);
                alert('削除に失敗しました');
            });
        });
        deleteBtn.classList.add('delete-btn');
        deleteBtn.dataset.categoryId = category.id; // 後で削除API呼ぶ用
        tdDelete.appendChild(deleteBtn);

        tr.appendChild(tdName);
        tr.appendChild(tdColor);
        tr.appendChild(tdEdit);
        tr.appendChild(tdDelete);

        tbody.appendChild(tr);
    });
})
.catch(err => {
    console.error('Failed to fetch todos:', err);
});

// 新規カテゴリー作成
// フォーム要素を取得
const form = document.getElementById('category-form');
// submitイベントをリッスン
form.addEventListener('submit', function(event) {
    // 通常のフォーム送信（ページ遷移）をキャンセル
    event.preventDefault();

    // 入力値の取得
    const name = document.getElementById('name').value;
    const color = document.getElementById('color').value;

    // フォームsubmit時に、その変数が null なら POST /api/categories（新規作成）、IDが入っていれば PATCH /api/categories/{id}（更新）
    let url;
    let method;
    if (editingCategoryId == null) {
        url = '/api/categories';
        method = 'POST';
    } else {
        url = '/api/categories/' + editingCategoryId;
        method = 'PATCH';
    }

    fetchWithAuth(
        token,
        url,
        method,
        {
            name: name,
            color: color
        }
    )
    .then(() => window.location.reload()) // 成功したら一覧を再読み込み
    .catch(error => console.log('error:', error));
    // .catch(error => {
    //     // エラーメッセージを画面に表示
    //     const errorDiv = document.getElementById('error-message');
    //     errorDiv.textContent = '保存に失敗しました。';
    //     errorDiv.hidden = false;
    // });
});
