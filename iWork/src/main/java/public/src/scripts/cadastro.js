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


function register(){
    let nome = document.getElementById("nomeIn").value;
    let email = document.getElementById("emailIn").value;
    let telefone = document.getElementById("telefoneIn").value;
    telefone.replace(/[^0-9]/g, '');
    let senha = document.getElementById("senhaIn").value;
    let confirmacaoSenha = document.getElementById("senhaIn2").value;
    let cartao = document.getElementById("cartaoIn").value;
    let validade = document.getElementById("validadeIn").value;
    let cvv = document.getElementById("cvvIn").value;
    let type = getSelectedValue();
    if(email=="" || senha=="" || nome=="" || type=="" || type == undefined){
        alert("Preencha todos os campos");
        return;
    }
    if(cvv.length != 3){
        alert("CVV inválido");
        return;
    }
    if(senha != confirmacaoSenha){
        alert("As senhas não coincidem");
        return;
    }
    if(email.indexOf("@") == -1){
        alert("Email inválido");
        return;
    }
    
    if(type == "consumidor"){
        fetch("./cadastroConsumidor?nome="+nome+"&email="+email+"&telefone="+telefone+"&senha="+senha+"&cartao="+cartao+"&validade="+validade+"&cvv="+cvv)
        .then(response => {
            if(response.status == 200){
                alert("Consumidor cadastrado com sucesso");
                window.location.href = "./login.html";
            }
            else{
                alert("Email ou telefone já cadastrado");
            }
            return response.json();
        })
    }
    else{
        fetch("./cadastroPrestador?nome="+nome+"&email="+email+"&telefone="+telefone+"&senha="+senha)
        .then(response => {
            if(response.status == 200){
                alert("Prestador cadastrado com sucesso");
                window.location.href = "./login.html";
            }
            else{
                alert("Email ou telefone já cadastrado");
            }
            return response.json();
        })
    }
    
}


document.getElementById("cadastroBtn").addEventListener("click", e=>{
    e.preventDefault();
    register();
});