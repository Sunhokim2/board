
const List = () => {
  return (
    <div className="container mt-5">
      <div className="row">
        <table className="table table-hover">
          <thead>
            <tr>
              <th>번호</th><th>제목</th><th>작성자</th>
            </tr>
          </thead>
          <tbody>
            <tr th:each="board : ${list}" th:attr="id=${board.id}">
              <td th:text="${board.id}"></td>
              <td th:text="${board.title}"></td>
              <td th:text="${board.userId}"></td>
            </tr>
          </tbody>
        </table>
        <button type="button" className="btn btn-primary btn-block" id="write-btn"
            onClick={() => {
              window.location = "/board/write";
            }}>글쓰기</button>
      </div>
    </div>
  );
};

export default List;