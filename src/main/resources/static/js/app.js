// 認証チェック
const token = getAuthToken();

// ログアウトボタン
const logout = document.getElementById('logout');
logout.addEventListener('click', function(event) {
    // ローカルストレージにあるアクセストークンを削除
    localStorage.removeItem("accessToken");
    // ログイン画面に遷移
    window.location.href = '/login.html';
});

// 既存Todo更新の場合、該当Idをここに格納
let editingTodoId = null;

// Todo一覧取得
fetchWithAuth(token, url='/api/todos/search', method='POST', body={})
.then(data => {
    // 既存行をクリア（再検索・再描画で重複しないように）
    const tbody = document.getElementById('todo-list');
    tbody.innerHTML = '';

    data.todos.forEach(todo => {
        const tr = document.createElement('tr');

        // innerHTML で埋め込むとXSSの危険があるので、
        // 基本は textContent で安全にセットする

        const tdTitle = document.createElement('td');
        tdTitle.textContent = todo.title ?? '';

        const tdDescription = document.createElement('td');
        tdDescription.textContent = todo.description ?? '';

        const tdCategory = document.createElement('td');
        tdCategory.textContent = todo.category?.name ?? '';

        const tdDue = document.createElement('td');
        tdDue.textContent = todo.due ?? '';

        const tdPriority = document.createElement('td');
        tdPriority.textContent = todo.priority ?? '';

        const tdCompleted = document.createElement('td');
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.checked = todo.isCompleted ?? false;  // 現在の完了状態を反映

        checkbox.addEventListener('change', function() {
            var newValue = checkbox.checked;       // 新しい値（ユーザーが変更した後）
            var previousValue = !checkbox.checked; // 元の値（反転すれば良い）
            fetchWithAuth(
                token,
                '/api/todos/' + todo.id,
                'PATCH',
                {
                    title: todo.title,
                    isCompleted: newValue
                }
            )
            .catch(err => {
                console.error('Update failed:', err);
                alert('更新に失敗しました');
                checkbox.checked = previousValue;
            });
        });
        tdCompleted.appendChild(checkbox);

        const tdEdit = document.createElement('td');
        const editBtn = document.createElement('button');
        editBtn.type = 'button';
        editBtn.textContent = '編集';
        editBtn.classList.add('edit-btn');
        editBtn.addEventListener('click', function() {
            editingTodoId = todo.id;
            document.getElementById('title').value = todo.title;
            document.getElementById('description').value = todo.description;
            document.getElementById('category').value = todo.category?.id;
            document.getElementById('due').value = todo.due;
            document.getElementById('priority').value = todo.priority;
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
                '/api/todos/' + todo.id,
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
        deleteBtn.dataset.todoId = todo.id; // 後で削除API呼ぶ用
        tdDelete.appendChild(deleteBtn);

        tr.appendChild(tdTitle);
        tr.appendChild(tdDescription);
        tr.appendChild(tdCategory);
        tr.appendChild(tdDue);
        tr.appendChild(tdPriority);
        tr.appendChild(tdCompleted);
        tr.appendChild(tdEdit);
        tr.appendChild(tdDelete);

        tbody.appendChild(tr);
    });
})
.catch(err => {
    console.error('Failed to fetch todos:', err);
});

// 新規Todo作成
// フォーム要素を取得
const form = document.getElementById('todo-form');
// submitイベントをリッスン
form.addEventListener('submit', function(event) {
    // 通常のフォーム送信（ページ遷移）をキャンセル
    event.preventDefault();

    // 入力値の取得
    const title = document.getElementById('title').value;
    const description = document.getElementById('description').value;
    const due = document.getElementById('due').value;
    const priority = document.getElementById('priority').value;
    const categoryId = document.getElementById('category').value;

    // フォームsubmit時に、その変数が null なら POST /api/todos（新規作成）、IDが入っていれば PATCH /api/todos/{id}（更新）
    let url;
    let method;
    if (editingTodoId == null) {
        url = '/api/todos';
        method = 'POST';
    } else {
        url = '/api/todos/' + editingTodoId;
        method = 'PATCH';
    }

    console.log('url:', url, 'method:', method, 'categoryId:', categoryId);
    fetchWithAuth(
        token,
        url,
        method,
        {
            title: title,
            description: description,
            due: due,
            priority: priority,
            categoryId: categoryId
        }
    )
    .then(() => window.location.reload()) // 成功したら一覧を再読み込み
    .catch(error => {
        // エラーメッセージを画面に表示
        const errorDiv = document.getElementById('error-message');
        errorDiv.textContent = '保存に失敗しました。';
        errorDiv.hidden = false;
    });
});

// 4. カテゴリの選択肢取得
fetchWithAuth(
    token,
    '/api/categories',
    'GET',
    {}
)
.then(data => {
    const select = document.getElementById('category');
    data.forEach(_category => {
        const option = document.createElement('option');
        option.value = _category.id;
        option.textContent = _category.name;
        select.appendChild(option);
    });
    // カテゴリ取得できた場合はセレクトボックスを選択可能にする
    select.disabled = false;
    // カテゴリ取得できた場合は「読み込み中...」のオプションを削除
    const loadingOption = document.getElementById('loading-option');
    loadingOption.remove();
    // カテゴリ取得できた場合は「選択してください」を初期表示用のプレースホルダーとして設定
    const option = document.createElement('option');
    option.selected = true;
    option.disabled = true;
    option.textContent = "選択してください";
    option.value = ""; // 送信時に空文字にする
    select.appendChild(option);
})
.catch(err => {
    console.error('Category failed:', err);
    alert('カテゴリ取得に失敗しました');
});
