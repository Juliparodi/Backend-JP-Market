const sendButton = document.getElementById("send");
const prompt = document.getElementById("prompt");
const messages = document.getElementById("messages");

sendButton.addEventListener("click", sendMessage);

async function sendMessage(){

    const text = prompt.value.trim();

    if(text === "")
        return;

    append("user", text);

    prompt.value="";

    const response = await fetch("/api/chat",{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:JSON.stringify({
            message:text
        })
    });

    const answer = await response.text();

    append("assistant", answer);
}

function append(css,text){

    const div=document.createElement("div");

    div.className=css;

    div.innerText=text;

    messages.appendChild(div);

    messages.scrollTop=messages.scrollHeight;
}