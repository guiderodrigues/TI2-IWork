let prestadorG;

function getLogin(){
    let login = JSON.parse(localStorage.getItem("login"));
    if(login == null){
        window.location.href = "login.html";
    }

    return login;
}

async function getPrestador(){
    /* if(prestadorG != undefined && prestadorG != null){
        return prestadorG;
    } */
    let login = getLogin();
    let id = login.id;

    let prestador = await fetch("/prestador/"+id).then(data => data.json());
    return prestador;
}


async function getServicosPrestador(){
    let idPrestador = getLogin().id;
    let servicos = await fetch("/servicosPrestador/"+idPrestador).then(data => data.json());
    return servicos;
}

async function getServicosPrestador(start, end){
    let idPrestador = getLogin().id;
    let servicos = await fetch("/servicosPrestador/"+idPrestador+"?start="+start+"&end="+end).then(data => data.json());
    return servicos;
}

async function getHorariosPrestadorDia(diaDaSemana){
    let idPrestador = getLogin().id;
    let horarios = await fetch("/diasDaSemana/"+idPrestador+"/"+diaDaSemana).then(data => data.json());
    return horarios;
}

function getDiaDaSemana(data){
    let dia = new Date(data).getDay();
    return dia;
}

function getDateString(data){
    let dia = data.getDate();
    let mes = data.getMonth()+1;
    let ano = data.getFullYear();
    return ano+"-"+mes+"-"+dia;
}

async function getHorariosPrestadorDisponiveisDia(dia){
    let diaDaSemana = getDiaDaSemana(dia);
    let horariosDisponiveis = await getHorariosPrestadorDia(diaDaSemana);
    let horariosDisponiveisArray = separate(horariosDisponiveis.horas);
    let servicosPrestador = await getServicosPrestador(getDateString(dia), getDateString(dia));
    for(let i=0; i<servicosPrestador.length; i++){
        for(let j=0;j<horariosDisponiveis.horas.length;j++){
            if(servicosPrestador[i].data == dia.toISOString().slice(0, 10) && servicosPrestador[i].hora == separate(horariosDisponiveis.horas)[j]){
                horariosDisponiveisArray.splice(j, 1);
            }
        }
    }

    return horariosDisponiveisArray;
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
        let qtdHorariosDisponiveis = (separate((await getHorariosPrestadorDia(dia.getDay())).horas).length) - ((await getHorariosPrestadorDisponiveisDia(dia)).length);
        document.getElementsByClassName("availableTimes")[i].innerHTML = qtdHorariosDisponiveis;
        dia = new Date(dia.getFullYear(), dia.getMonth(), dia.getDate()+1);
    }
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
    console.log(horariosPossiveis);
    let horariosDisponiveis = await getHorariosPrestadorDisponiveisDia(day);
    console.log(horariosDisponiveis);
    let dayInfoTitle =  document.getElementById("dayInfoTitle");
    let availableTimesDiv = document.getElementById("horariosDisponiveis");
    dayInfoTitle.innerHTML = "Dia "+ addZeroToNumber(day.getDate()) + "/" + addZeroToNumber(day.getMonth()+1)
    availableTimesDiv.innerHTML = "";
    for(let i=0; i<horariosPossiveis.length; i++){
        let horarioDisponivel = document.createElement("div");
        horarioDisponivel.classList.add("horarioDisponivel");
        if(!horariosDisponiveis.includes(horariosPossiveis[i])){
            horarioDisponivel.addEventListener("click", async ()=>{
                let servicos = await getServicosPrestador(getDateString(day), getDateString(day));
                let servico;
                for(let i=0; i<servicos.length; i++){
                    if(servicos[i].data == getDateString(day) && servicos[i].hora == horarioDisponivel.innerHTML){
                        servico = servicos[i];
                    }
                }

                let consumidor = await fetch("/consumidor/"+servico.idConsumidor).then(data => data.json());

                document.getElementById("nomeConsumidor").innerHTML = consumidor.nome;
                if(servico.endereco == "" || servico.endereco == undefined || servico.endereco == null){
                    document.getElementById("enderecoConsumidor").innerHTML = "Endereço não informado";
                }else{
                document.getElementById("enderecoConsumidor").innerHTML = servico.endereco;
                }
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

function separate(imagens){
    let imgs = [];
    imgs = imagens.split(",");
    imgs.pop();
    return imgs;
}

async function putPrestadorInfo(){
    let prestador = await getPrestador();
    let categorias = [
        "Limpeza Geral da Casa",
        "Organização de Armários e Despensas",
        "Lavagem e Passagem de Roupas",
        "Banho e Tosa",
        "Passeio com Cachorros",
        "Hospedagem Temporária",
        "Eletricista",
        "Pintura e Reformas",
        "Instalação de Móveis e Prateleiras",
        "Preparação de Refeições",
        "Churrasqueiro",
        "Doces e Bolos",
        "Garçons e Bartenders",
        "Cuidador de Idosos",
        "Acompanhamento de Pessoas com Necessidades Especiais",
        "Acompanhamento Hospitalar",
        "Fisioterapeuta Domiciliar",
        "Decoradores",
        "Fotógrafos",
        "Criação de Convites",
        "Mesas, Cadeiras e Utensílios",
        "Músicos e DJs",
        "Aluguel de Brinquedos e Jogos",
        "Animadores"
    ]


    document.getElementById("perfilTitle").innerHTML = (prestador.nome!=undefined)?prestador.nome:"Título ainda não informado";
    document.getElementById("perfilDesc").innerHTML = (prestador.descricao!=undefined && prestador.descricao != "")?prestador.descricao:"Descrição ainda não informada";
    document.getElementById("perfilPreco").innerHTML = (prestador.preco!=undefined)?("R$"+prestador.preco):"Preço ainda não informado";
    document.getElementById("perfilCategoria").innerHTML = (prestador.categoria!=undefined)?categorias[prestador.categoria]: "Categoria ainda não informada";
    document.getElementById("imgsDiv").innerHTML = "";
    if(prestador.imagens && prestador.imagens.length != 0){
        let imagesArray = separate(prestador.imagens);
        imagesArray.forEach(img => {
            let imgDiv = document.createElement("div");
            imgDiv.classList.add("imgDiv");
            imgDiv.innerHTML = `
                <img class="perfilImg" src="${img}">
                <img class="trashImg" src="src/img/trash.png">
            `
            imgDiv.children[1].addEventListener("click", ()=>{
                document.getElementById("imgsDiv").removeChild(imgDiv);
            })
            document.getElementById("imgsDiv").appendChild(imgDiv);
        })
    }
    let addImgBtn = document.createElement("button");
    addImgBtn.id = "addImgBtn";
    addImgBtn.innerHTML = "+";
    addImgBtn.addEventListener("click", ()=>{
        if(document.getElementsByClassName("imgDiv").length >= 4){
            alert("Limite de imagens atingido");
            return;
        }
        let img = prompt("Insira o link da imagem");
        if(img == null || img == ""){
            alert("Insira um link válido");
            return;
        }
        let imgDiv = document.createElement("div");
        imgDiv.classList.add("imgDiv");
        imgDiv.innerHTML = `
            <img class="perfilImg" src="${img}">
            <img class="trashImg" src="src/img/trash.png">
        `
        document.getElementById("imgsDiv").appendChild(imgDiv);
    })
    document.getElementById("imgsDiv").appendChild(addImgBtn);
}


async function putDiasDaSemana(){
    let diasObj = await fetch("/diasDaSemana/"+getLogin().id).then(data => data.json());
    let dias = []

    for(let i=0;i<7;i++){
        dias.push(diasObj[i].horas.split(","));
        dias[i].pop();
    }

    let horarioElts = document.getElementsByClassName("horario");
    for(let i=0;i<dias.length;i++){
        for(let j=7;j<=23;j++){
            if(dias[i].includes(j+":00")){
                horarioElts[(j-7)*7+i].classList.remove("disabledHorario");
            }
        }
    }
}

putPrestadorInfo();

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

document.getElementById("perfilSaveBtn").addEventListener("click", async ()=>{
    let prestador = await getPrestador();
    if(document.getElementById("perfilNewTitle").value!= '') prestador.nome = document.getElementById("perfilNewTitle").value;
    if(document.getElementById("perfilNewDesc").value!= '')prestador.descricao = document.getElementById("perfilNewDesc").value;
    if(document.getElementById("perfilNewPreco").value!= ''){
        if(document.getElementById("perfilNewPreco").value.split("R$")[1] == undefined){
            prestador.preco = parseFloat(document.getElementById("perfilNewPreco").value);
        }else{
            prestador.preco = parseFloat(document.getElementById("perfilNewPreco").value.split("R$")[1]);
        }
    }
    if(document.getElementById("perfilNewCategoria").value != "categoria"){
        prestador.categoria = document.getElementById("perfilNewCategoria").value;
    }

    prestador.imagens = [];
    let imgs = document.getElementsByClassName("perfilImg");
    if(imgs.length > 4){
        alert("Limite de imagens atingido");
        return;
    }
    for(let i=0; i<imgs.length; i++){
        prestador.imagens += imgs[i].src + ",";
    }

    let login = getLogin();
    let token = login.token;
    let id = login.id;

    let res = await fetch("/updatePrestador?id="+ id + "&nome=" + prestador.nome + "&descricao=" + prestador.descricao + "&preco=" + prestador.preco + "&categoria=" + prestador.categoria + "&imagens=" + prestador.imagens + "&token=" + token).then(data => data.json());
    if(res.status == "ok"){
        alert("Alterações guardadas com sucesso!");
    }else if(res.status == "not_authenticated"){
        alert("Sessão expirada");
        localStorage.removeItem("login");
        window.location.href = "login.html";
    }else{
        alert("Ocorreu um erro");
    }
    
    putPrestadorInfo();
})

document.getElementById("horariosConfirmBtn").addEventListener("click", async ()=>{
    let horarioElts = document.getElementsByClassName("horario");
    let allHorarios = [[],[],[],[],[],[],[]];

    for(let i=0;i<17;i++){
        for(let j=0;j<7;j++){
            if(!(horarioElts[i*7+j].classList.contains("disabledHorario"))){
                allHorarios[j].push((i+7)+":00");
            }
        }
    }

    let reqStr = "";
    let login = getLogin();
    reqStr += "?id="+login.id+"&token="+login.token;

    for(let i=0;i<7;i++){
        reqStr += "&dia"+i+"=";
        for(let j=0;j<allHorarios[i].length;j++){
            reqStr += allHorarios[i][j] + ",";
        }
    }

    let res = await fetch("/updateHorarios" + reqStr).then(data => data.json());
    if(res.status == "not_authenticated"){
        alert("Sessão expirada");
        localStorage.removeItem("login");
        window.location.href = "login.html";
    }else if(res.status == "ok"){
        alert("Alterações guardadas com sucesso!");
    }else{
        alert("Ocorreu um erro");
    }

    putDiasDaSemana();
})

for(let i=0;i<119;i++){
    document.getElementsByClassName("horario")[i].addEventListener("click", ()=>{
        if(document.getElementsByClassName("horario")[i].classList.contains("disabledHorario")){
            document.getElementsByClassName("horario")[i].classList.remove("disabledHorario");
        }else{
            document.getElementsByClassName("horario")[i].classList.add("disabledHorario");
        }
    })
}

putCurrentMonth(dataMostrada);
putDaysCalendar(new Date());
putQtdHorariosDisponiveisCalendar(new Date());
selectDate(new Date());
showUnavailableDays();
showUnavailableTimes();
putDiasDaSemana();