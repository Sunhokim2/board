const Signin = () => {
  return (
    <div className="container mt-5">
      <div className="row">
        <form method="post" action="/signin">
          <div className="mb-3 input-group flex-nowrap">
            <span className="input-group-text">💻</span>
            <input type="text" className="form-control" name="email" placeholder="email"></input>
          </div>
          <div className="mb-3 input-group flex-nowrap">
            <span className="input-group-text">🔒</span>
            <input type="password" className="form-control" name="pwd" placeholder="password"></input>
          </div>
          <div className="d-grid gap-2">
            <button className="btn btn-primary" id="signin">Sign In</button>
          </div>
          <div className="d-grid gap-2">
            <button type="button" className="btn btn-secondary" id="signup" onClick={() => {
              window.location = "/signup";
            }}>Sign Up</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Signin;