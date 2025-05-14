const Update = () => {
  return (
    <>
      <div className="container mt-5">
        <div className="card">
          <div className="card-body">
            <span className="badge bg-primary rounded-pill" th:text="${board.id} + '번'"></span>
            <h5 className="card-title" th:text="${board.title}"></h5>
            <h6 className="card-subtitle mb-2 text-muted" th:text="${board.userId}"></h6>
            <p className="card-text" th:text="${board.content}"></p>
          </div>
        </div>
      </div>
      <div className="container mt-5">
        <ul className="nav justify-content-end">
          <li className="nav-item">
            <a className="nav-link" href="#" id="list">목록</a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href="#" id="update">수정</a>
          </li>
          <li className="nav-item">
            <a className="nav-link" href="#" id="delete" th:num="${board.id}">삭제</a>
          </li>
        </ul>
      </div>
    </>
  );
};

export default Update;