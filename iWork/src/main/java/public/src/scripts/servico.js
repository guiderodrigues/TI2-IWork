function getPageId(){
    let url = window.location.href;
    let pageId;
    try{
        let urlParams = url.split("?");
        pageId = urlParams[1].split("=")[1];
    }catch{
        pageId = 1;
    }
    
    return pageId;
}

function createStarsElt(nota){
    let starsImg = document.createElement("img");
    starsImg.classList.add("estrelasImg");
    starsImg.src = "https://img.freepik.com/vetores-premium/icone-de-classificacao-de-cinco-estrelas-estrelas-de-avaliacao-vetor-estrelas-planas-isoladas_118339-1270.jpg?w=2000";
    starsImg.style.clipPath = "inset(0 " + (100-nota*20) + "% 0 0)";
    return starsImg;
}

function getLogin(){
    let login = JSON.parse(localStorage.getItem("login"));
    return login;
}


async function getPrestador(){
    let prestador = await (await fetch("/prestador/" + getPageId())).json();
    return prestador;
}

function getServicosPrestador(){
    let idPrestador = getPageId();
    let servicosPrestador = [];
    for(let i=0; i<servicos.length; i++){
        if(servicos[i].idPrestador == idPrestador){
            servicosPrestador.push(servicos[i]);
        }
    }
    return servicosPrestador;
}

async function getHorariosPrestadorDia(diaDaSemana){
    let id = getPageId();
    let horarios = await fetch("/diasDaSemana/" + id + "/" + diaDaSemana).then(res => res.json());
    return horarios;
}

async function getServicosPrestadorDia(dia){
    let servicosPrestador = await fetch("/servicosPrestador/" + getPageId() + "?start=" + dia.getFullYear() + "-" + (dia.getMonth()+1) + "-" + dia.getDate() + "&end=" + dia.getFullYear() + "-" + (dia.getMonth()+1) + "-" + (dia.getDate())).then(res => res.json());
    return servicosPrestador;
}
function getDiaDaSemana(data){
    let dia = new Date(data).getDay();
    return dia;
}

function separate(imagens){
    let imgs = [];
    imgs = imagens.split(",");
    imgs.pop();
    return imgs;
}

async function getHorariosPrestadorDisponiveisDia(dia){
    let diaDaSemana = getDiaDaSemana(dia);
    let horariosDia = separate((await getHorariosPrestadorDia(diaDaSemana)).horas);
    let servicosPrestadorDia = await getServicosPrestadorDia(dia);

    let horariosDisponiveis = [];

    console.log(diaDaSemana + " " + horariosDia);
    console.log(servicosPrestadorDia);

    for(let i=0; i<horariosDia.length; i++){
        let horarioDisponivel = true;
        for(let j=0; j<servicosPrestadorDia.length; j++){
            if(horariosDia[i] == servicosPrestadorDia[j].hora){
                horarioDisponivel = false;
                break;
            }
        }
        if(horarioDisponivel){
            horariosDisponiveis.push(horariosDia[i]);
        }
    }

    return horariosDisponiveis;
}

function getDiasLimitesMes(data){
    let primeiroDia = new Date(data.getFullYear(), data.getMonth(), 1);
    let primeiroDiaSemana = primeiroDia.getDay();
    let primeiroDiaCalendario = new Date(primeiroDia.getFullYear(), primeiroDia.getMonth(), primeiroDia.getDate() - primeiroDiaSemana);
    let ultimoDiaCalendario = new Date(primeiroDiaCalendario.getFullYear(), primeiroDiaCalendario.getMonth(), primeiroDiaCalendario.getDate() + 34);
    return {
        primeiro: primeiroDiaCalendario,
        ultimo: ultimoDiaCalendario
    }
}



async function putPrestador(){
    let prestador = await getPrestador();
    let prestadorDiv = document.getElementById("service");
    let images = separate(prestador.imagens);
    prestadorDiv.innerHTML = `
    <div id="imgDiv">
        <img id="mainImg" src="${images[0]}">
        <div id="miniImgs">
        </div>
    </div>
    <div id="info">
        <h1 id="title">${prestador.nome}</h1>
        <p id="description">${prestador.descricao}</p>
        <div id="rating"></div>
        <span id="price">R$${prestador.preco}/Hora</span>
        <button id="redirectButton">Contratar</button>
    </div>
    `
    for(let i=0; i<images.length; i++){
        let miniImg = document.createElement("img");
        miniImg.src = images[i];
        miniImg.className = "miniImg";
        document.getElementById("miniImgs").appendChild(miniImg);
    }
    document.getElementById("rating").appendChild(createStarsElt(prestador.nota));
    for(let i=0; i<document.getElementsByClassName("miniImg").length;i++){
        document.getElementsByClassName("miniImg")[i].addEventListener("mouseover", function(){
            document.getElementById("mainImg").src = document.getElementsByClassName("miniImg")[i].src;
            document.getElementsByClassName("miniImg")[i].style.border = "5px solid #000";
            for(let j=0; j<document.getElementsByClassName("miniImg").length;j++){
                if(j!=i){
                    document.getElementsByClassName("miniImg")[j].style.border = "3px solid grey";
                }
            }
        })
    }
    document.getElementById("hirePrice").innerHTML = "R$"+prestador.preco;
    document.getElementById("bubbleDiv").addEventListener("click", ()=>{
        window.open("https://wa.me/"+prestador.telefone, "_blank");
    })
}

function putDaysCalendar(day){
    let daysSpans = document.getElementsByClassName("dayDisplay");
    let diasLimites = getDiasLimitesMes(day);
    let dia = diasLimites.primeiro;
    for(let i=0; i<daysSpans.length; i++){
        daysSpans[i].innerHTML = dia.getDate();
        daysSpans[i].dataset.date = dia;
        dia = new Date(dia.getFullYear(), dia.getMonth(), dia.getDate()+1);
    }
}

async function putQtdHorariosDisponiveisCalendar(day){
    let diasLimites = getDiasLimitesMes(day);
    let dia = diasLimites.primeiro;
    for(let i=0; i<35; i++){
        let qtdHorariosDisponiveis = (await getHorariosPrestadorDisponiveisDia(dia)).length;
        document.getElementsByClassName("availableTimes")[i].innerHTML = qtdHorariosDisponiveis;
        dia = new Date(dia.getFullYear(), dia.getMonth(), dia.getDate()+1);
    }
    showUnavailableDays()
}

function addZeroToNumber(number){
    if(number<10){
        return "0"+number;
    }else{
        return number;
    }
}

async function selectDate(day){
    let diaDaSemana = getDiaDaSemana(day);
    let horariosPossiveis = separate((await getHorariosPrestadorDia(diaDaSemana)).horas);
    let horariosDisponiveis = await getHorariosPrestadorDisponiveisDia(day);
    let dayInfoTitle =  document.getElementById("dayInfoTitle");
    let availableTimesDiv = document.getElementById("horariosDisponiveis");
    dayInfoTitle.innerHTML = "Dia "+ addZeroToNumber(day.getDate()) + "/" + addZeroToNumber(day.getMonth()+1)
    availableTimesDiv.innerHTML = "";
    for(let i=0; i<horariosPossiveis.length; i++){
        let horarioDisponivel = document.createElement("div");
        horarioDisponivel.classList.add("horarioDisponivel");
        if(horariosDisponiveis.includes(horariosPossiveis[i])){
            horarioDisponivel.addEventListener("click", ()=>{
                document.getElementById("hireTime").innerHTML = `Dia ${addZeroToNumber(day.getDate())}/${addZeroToNumber(day.getMonth()+1)} às ${horarioDisponivel.innerHTML}`
            });
        }else{
            horarioDisponivel.classList.add("horarioIndisponivel");
        }
        horarioDisponivel.innerHTML = horariosPossiveis[i];
        availableTimesDiv.appendChild(horarioDisponivel);
    }
}

function putCurrentMonth(date){
    let months = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"];
    document.getElementById("mesMostrado").innerHTML = months[date.getMonth()]+" de "+date.getFullYear();
}

function showUnavailableDays(){
    let today = new Date(new Date().getTime() - 86400000);
    let calendarDay = document.getElementsByClassName("calendarDay");
    let dayDisplays = document.getElementsByClassName("dayDisplay");
    let availableTimes = document.getElementsByClassName("availableTimes");
    for(let i=0; i<calendarDay.length; i++){
        if(new Date(dayDisplays[i].dataset.date) < today){
            calendarDay[i].classList.add("unavailableDay");
            availableTimes[i].innerHTML = "-";
        }
        else{
            calendarDay[i].classList.remove("unavailableDay");
        }
    }
}

function showUnavailableTimes(){
    let horariosDisponiveis = document.getElementsByClassName("horarioDisponivel");
    let day = {"dia": 0, "mes": 0, "ano": 0};
    day.dia = document.getElementById("dayInfoTitle").innerHTML.split(" ")[1].split("/")[0];
    day.mes = document.getElementById("dayInfoTitle").innerHTML.split(" ")[1].split("/")[1];
    day.ano = document.getElementById("mesMostrado").innerHTML.split(" ")[2];
    for(let i=0;i<horariosDisponiveis.length;i++){
        let horario = {"hora": horariosDisponiveis[i].innerHTML.split(":")[0], "minuto": horariosDisponiveis[i].innerHTML.split(":")[1]};
        let data = new Date(day.ano, day.mes-1, day.dia, horario.hora, horario.minuto);
        if(data < new Date()){
            horariosDisponiveis[i].classList.add("horarioIndisponivel");
        }
    }
}


async function getAvaliacoes(){
    let idPrestador = getPageId();
    let avaliacoes = await fetch("/avaliacoes/" + idPrestador).then(res => res.json());

    return avaliacoes;
}

async function putAvaliacoes(){
    let avaliacoes = await getAvaliacoes();
    let avaliacoesDiv = document.getElementById("avaliacoes");
    avaliacoesDiv.innerHTML = "";
    for(let i=0;i<avaliacoes.length;i++){    
        let avaliacaoElt = document.createElement("div");
        avaliacaoElt.classList.add("avaliacao");
        avaliacaoElt.innerHTML = `
            <div class="avaliacaoInfo">
                <div class="avaliacaoNota"></div>
                <div class="avaliacaoData">
                    <span class="avaliacaoDataDay">${avaliacoes[i].data} às ${avaliacoes[i].hora}</span>
                </div>
            </div>
        `

        if(avaliacoes[i].comentario != null && avaliacoes[i].comentario != undefined && avaliacoes[i].comentario != ""){
            avaliacaoElt.innerHTML += `<div class="avaliacaoComentario">
                <p class="avaliacaoComentarioText">${avaliacoes[i].comentario}</p>
            </div>`
        }else{
            avaliacaoElt.getElementsByClassName("avaliacaoInfo")[0].style.borderBottom = "none";
            avaliacaoElt.style.paddingBottom = "0";
        }
        avaliacaoElt.getElementsByClassName("avaliacaoNota")[0].appendChild(createStarsElt(avaliacoes[i].nota));
        avaliacoesDiv.appendChild(avaliacaoElt);
    }
}   




for(let i=0;i<35;i++){
    document.getElementsByClassName("calendarDay")[i].addEventListener("click", ()=>{
        if(!document.getElementsByClassName("calendarDay")[i].classList.contains("unavailableDay")){
            selectDate(new Date(document.getElementsByClassName("dayDisplay")[i].dataset.date));
            showUnavailableTimes();
        }
    })   
}

let dataMostrada = new Date();

document.getElementById("prev").addEventListener("click", ()=>{
    dataMostrada = new Date(dataMostrada.getFullYear(), dataMostrada.getMonth()-1, 1);
    putDaysCalendar(dataMostrada);
    putQtdHorariosDisponiveisCalendar(dataMostrada);
    putCurrentMonth(dataMostrada);
    showUnavailableDays()
})

document.getElementById("next").addEventListener("click", ()=>{
    dataMostrada = new Date(dataMostrada.getFullYear(), dataMostrada.getMonth()+1, 1);
    putDaysCalendar(dataMostrada);
    putQtdHorariosDisponiveisCalendar(dataMostrada);
    putCurrentMonth(dataMostrada);
    showUnavailableDays()
})

document.getElementById("hireButton").addEventListener("click", async ()=>{
    let id = getLogin().id;
    let token = getLogin().token;
    if(id == null || id == undefined || id == "" || token == null || token == undefined || token == ""){
        alert("Faça login para contratar um serviço");
        window.location.href = "login.html";
        return;
    }
    if(document.getElementById("hireTime").innerHTML == "Selecione um horário"){
        alert("Selecione um horário");
    }else{
        let dia = document.getElementById("dayInfoTitle").innerHTML.split(" ")[1].split("/")[0]
        let mes = document.getElementById("dayInfoTitle").innerHTML.split(" ")[1].split("/")[1]
        let ano = document.getElementById("mesMostrado").innerHTML.split(" ")[2]
        
        let data = ano+"-"+mes+"-"+dia
        let hora = document.getElementById("hireTime").innerHTML.split(" ")[3]
        
        let endereco = document.getElementById("hireEndereco1").value + " " + document.getElementById("hireEndereco2").value;

        let status = await fetch("/newServico" + "?idConsumidor=" + id + "&token=" + token + "&idPrestador=" + getPageId() + "&data=" + data + "&hora=" + hora + "&endereco="+endereco).then(res => res.json());
        if(status.status == "error"){
            alert("Erro ao contratar serviço");
            return;
        }
        if(status.status == "not_authenticated"){
            alert("Faça login para contratar um serviço");
            window.location.href = "login.html";
            return;
        }
        document.getElementById("dialogDesc").innerHTML = `O serviço ${(await getPrestador()).nome} será realizado ${document.getElementById("hireTime").innerHTML.toLowerCase()}`;
        document.getElementsByTagName("dialog")[0].showModal()
    }
})



putCurrentMonth(dataMostrada);
putPrestador(getPageId());
putDaysCalendar(new Date());
putQtdHorariosDisponiveisCalendar(new Date())
putAvaliacoes();

selectDate(new Date())
.then(showUnavailableTimes())

document.getElementById("redirectButton").addEventListener("click", e=>{
    e.preventDefault();
    window.scrollTo({ top: 900, behavior: 'smooth' })
})