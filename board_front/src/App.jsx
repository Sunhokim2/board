import './styles/App.css'

import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Header from './components/Header';
import Nav from './components/Nav';
import Footer from './components/Footer';
import Index from './pages/Index';
import Signin from './pages/Signin';
import Signup from './pages/Signup';
import List from './pages/List';
import View from './pages/View';
import Write from './pages/Write';
import Update from './pages/Update';
import BoardLayout from './components/BoardLayout';

function App() {
  return (
    <>
      <BrowserRouter>
      <Header />
      <Nav />
        <Routes>
          <Route path='/' element={<Index />}></Route>
          <Route path='/index' element={<Index />}></Route>
          <Route path='/signin' element={<Signin />}></Route>
          <Route path='/signup' element={<Signup />}></Route>
          
          {/* useEffect도 컴포넌트에서 작동?이라한다. */}
          <Route path='/board' element={<BoardLayout />}>
            <Route path='list' element={<List />}></Route>
            <Route path='view' element={<View />}></Route>
            <Route path='write' element={<Write />}></Route>
            <Route path='update' element={<Update />}></Route>
          </Route>
          
        </Routes>
      <Footer />
      </BrowserRouter>
    </>
  );
};

export default App
