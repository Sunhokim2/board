import { Link } from "react-router-dom";

const Nav = () => {
  return (
    <nav className="navbar navbar-expand-sm bg-dark navbar-dark">
      <div className="container-fluid">
        <ul className="navbar-nav">
          <li className="nav-item">
            {/* Link쓰면 페이지는 그대로 두고 화면만바뀐다 */}
            {/* SPA - Single Page Application */}
            <Link className="nav-link active" to="/">Home</Link>
          </li>
          <li className="nav-item">
            <Link className="nav-link" to="/board/list">게시판</Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};

export default Nav;