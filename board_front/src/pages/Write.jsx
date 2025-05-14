const Write = () => {
  return (
    <div className="container mt-5">
      <div className="row">
        <form method="post" action="/board/write">
          <div className="mb-3">
            <label htmlFor="title">Title:</label>
            <input type="text" className="form-control" id="title" name="title"></input>
          </div>
          <div className="mb-3">
            <label htmlFor="content">Content:</label>
            <textarea className="form-control" rows="5" name="content" id="content"></textarea>
          </div>
          <div className="d-grid gap-2">
            <button className="btn btn-primary" id="complete">글쓰기</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Write;