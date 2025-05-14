const API_BASE_URL = "http://localhost:8080/api/user";

export const createUser =async (json)=>{
    // ❗
    console.log("json ",json);

    const url = API_BASE_URL + "/signup";
    
    const res = await fetch(url,{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: json
    });

    const data = await res.json(); // 한번 읽으면 다시 읽으면 안됩니다.
    console.log("data ",data);

    if (!res.ok) {
    let errorData = { message: `요청 실패 상태 :  ${res.status}` }; // 기본 오류 메시지
    const parsedError = await res.json();
    errorData = { ...parsedError, message: parsedError.message || res.statusText };
    
  }

  return data;
}