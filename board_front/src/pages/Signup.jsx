import { createUser } from "../api/user_api";
import React, { useState } from 'react';

const Signup = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  
  return (
    <div className="container mt-5">
      <div className="row">
        <form method="post" action="/signup">

          <div className="mb-3">
            <label htmlFor="email">Email:</label>
            <input type="text" className="form-control" id="email"
             name="email" value={email} onChange={(e)=>{
              setEmail(e.target.value);
             }}></input>
          </div>

          <div className="mb-3">
            <label htmlFor="pwd">Password:</label>
            <input type="password" className="form-control" id="pwd" name="pwd"
            value={password} onChange={(e)=>{
              setPassword(e.target.value);
            }}></input>
          </div>


          <div className="mb-3">
            <label htmlFor="name">Name:</label>
            <input type="text" className="form-control" id="name" name="name"
            value={name} onChange={(e)=>{
              setName(e.target.value);
            }}></input>
          </div>

          <div className="d-grid gap-2">
            <button className="btn btn-primary" id="signup"
            onClick={(e)=>{
              e.preventDefault();
              
              const json = JSON.stringify({
                email: email,
                pwd: password,
                name: name
              });

              const result=createUser(json);
              if(result){
                alert("회원가입 성공");
                // window.location = "/signin";
              }
              else{
                alert("회원가입 실패");
              }
            }}>Sign Up</button>
          </div>

        </form>
      </div>
    </div>
  );
};

export default Signup;