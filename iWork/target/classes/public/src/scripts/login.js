function getSelectedValue() {
    let radios = document.querySelectorAll('input[name="type"]');
    let selectedValue;
  
    for (let i = 0; i < radios.length; i++) {
      if (radios[i].checked) {
        selectedValue = radios[i].value;
        break;
      }
    }
    return selectedValue;
}

function getUsers(){
    let prestadores = getPrestadores();
    prestadores.forEach(prestador => prestador.type = "prestador");
    let consumidores = getConsumidores();
    consumidores.forEach(consumidor => consumidor.type = "consumidor");
    let users = prestadores.concat(consumidores);
    return users;
}

function login(){
    let email = document.getElementById("emailIn").value;
    let senha = document.getElementById("senhaIn").value;
    let type = getSelectedValue();
    if(email=="" || senha=="" || type=="" || type == undefined){
        alert("Preencha todos os campos");
        return;
    }

    if(type == "consumidor"){
        fetch("./loginConsumidor?email="+email+"&senha="+senha)
        .then(response => {
            if(response.status == 200){
                response.json().then(token => {
                    localStorage.setItem("login", JSON.stringify(token));
                    window.location.href = "./pesquisa.html";
                });
            }else{
                alert("Email ou senha incorretos");
            }
        });
    }else if(type == "prestador"){
        fetch("./loginPrestador?email="+email+"&senha="+senha)
        .then(response => {
            if(response.status == 200){
                response.json().then(token => {
                    localStorage.setItem("login", JSON.stringify(token));
                    window.location.href = "./prestador.html";
                });
            }else{
                alert("Email ou senha incorretos");
            }
        });
    }
}


document.getElementById("loginBtn").addEventListener("click", e=>{
    e.preventDefault();
    login();
});