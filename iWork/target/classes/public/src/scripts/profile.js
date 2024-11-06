function getLogin(){
    return JSON.parse(localStorage.getItem('login'));
}



async function getProfileServices(){
    let login = getLogin();
    
    if(login.type == 'prestador'){
        alert('Você não tem permissão para acessar essa página');
        window.location.href = './index.html';
        return;
    }

    let res = await fetch("/servicosConsumidor/" + login.id).then(res => res.json());

    return res;
}

function separate(imagens){
    let imgs = [];
    imgs = imagens.split(",");
    imgs.pop();
    return imgs;
}

async function getPrestadorById(id){
    let res = await fetch("/prestador/" + id).then(res => res.json());
    return res;
}

async function putServices(){
    let services = await getProfileServices();
    let prestadores = [];
    for(let i=0;i<services.length;i++){  
        prestadores.push(await getPrestadorById(services[i].idPrestador));
    }
    document.getElementById('main').innerHTML = '';
    let i = 0;
    services.forEach(service => {
        document.getElementById('main').innerHTML += `
        <div class="serviceDiv">
            <div class="serviceImgDiv">
                <img class="serviceImg" src="${separate(prestadores[i].imagens)[0]}">
            </div>
            <div class="serviceInfo">
                <h3 class="serviceName">${prestadores[i].nome}</h3>
                <p class="servicePrice">R$${prestadores[i].preco}</p>
                <p class="servicePrice data">${service.hora} ${service.data}</p>
                <ul class="serviceRating" data-idPrestador=${service.idPrestador}>
                    <img class="star" onclick='rate(${service.ID}, ${1})' onmouseover='appearRate(${i}, ${1})'" src="./src/img/emptyStar.png">
                    <img class="star" onclick='rate(${service.ID}, ${2})' onmouseover='appearRate(${i}, ${2})'" src="./src/img/emptyStar.png">
                    <img class="star" onclick='rate(${service.ID}, ${3})' onmouseover='appearRate(${i}, ${3})'" src="./src/img/emptyStar.png">
                    <img class="star" onclick='rate(${service.ID}, ${4})' onmouseover='appearRate(${i}, ${4})'" src="./src/img/emptyStar.png">
                    <img class="star" onclick='rate(${service.ID}, ${5})' onmouseover='appearRate(${i}, ${5})'" src="./src/img/emptyStar.png">
                </ul>
                <textarea class="serviceComment" onblur = "comment(${service.ID}, ${i})" placeholder="Deixe um comentário..."></textarea>
            </div>
        </div>
        `

        document.getElementsByClassName('serviceComment')[i].value = service.comentario;
        console.log(service.avaliacao)
        appearRate(i, service.avaliacao);
        i++;
    })
}


function comment(serviceID, i){
    console.log(document.getElementsByClassName('serviceComment')[i])
    let login = getLogin();
    let token = login.token;
    let comment = document.getElementsByClassName('serviceComment')[i].value;
    if(comment == '') comment = "undefined"
    let idPrestador = document.getElementsByClassName('serviceRating')[i].dataset.idprestador;
    fetch("/rate/" + serviceID + "?token=" + token + "&idPrestador=" + idPrestador + "&comentario=" + comment + "&nota=" + -1).then(res => res.json()).then(res => {
        if(res.status == 'ok'){
            alert('Comentário enviado com sucesso!');
        }else{
            alert('Erro ao enviar comentário');
        }
    })
}

async function rate(i, nota, comment){
    let login = getLogin();
    let token = login.token;
    
    let res = await fetch("/rate/" + i + "?nota=" + nota + "&token=" + token + "&comment="+comment).then(res => res.json());
    if(res.status == 'ok'){
        alert('Avaliação realizada com sucesso!');
        putServices();
    }else{
        alert('Erro ao avaliar');
    }
    
}

function appearRate(i, nota){
    let parent = document.getElementsByClassName('serviceRating')[i];
    let children = parent.children;
    for(let i = 0; i < children.length; i++){
        if(i < nota){
            children[i].src = './src/img/star.png';
        }else{
            children[i].src = './src/img/emptyStar.png';
        }
    }
}

function disappearAll(i){
    let parent = document.getElementsByClassName('serviceRating')[i];
    let children = parent.children;

    for(let i = 0; i < parent.children.length; i++){
        children[i].src = './src/img/emptyStar.png';
    }
}

putServices()