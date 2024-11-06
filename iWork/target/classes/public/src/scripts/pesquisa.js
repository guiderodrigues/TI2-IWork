function createStarsElt(nota){
    let starsImg = document.createElement("img");
    starsImg.classList.add("estrelasImg");
    starsImg.src = "./src/img/stars.png";
    starsImg.style.clipPath = "inset(0 " + (100-nota*20) + "% 0 0)";
    return starsImg;
}

function getPageCategory(){
    let url = window.location.href;
    let category;
    let categoryElts = document.getElementsByClassName("categoryLink");
    for(let i=0; i<categoryElts.length; i++){
        categoryElts[i].classList.remove("categorySelected");
    }
    try{
        let urlParams = url.split("?");
        category = urlParams[1].split("=")[1];
        for(let i=0; i<categoryElts.length; i++){
            if(categoryElts[i].innerHTML.toLowerCase().replace(/\s/g, "") == category){
                categoryElts[i].classList.add("categorySelected");
            }
        }
    }catch{
        category = -1;
        document.getElementById("noCategory").firstChild.classList.add("categorySelected");
    }
    return parseInt(category);
}

async function searchPrestadores(search, minPrice, maxPrice, order, categoria){
    let res = await fetch("/search?search=" + search + "&minPrice=" + minPrice + "&maxPrice=" + maxPrice + "&order=" + order + "&categoria=" + categoria)
    let prestadores = await res.json();
    return prestadores;
}

function limitText(text, limit){
    if(text.length > limit){
        return text.substring(0, limit-3) + "...";
    }
    return text;
}

function createServiceDiv(prestador){
    let div = document.createElement("div");
    div.classList.add("service");
    div.innerHTML = `
        <img class="serviceImg" src="${prestador.imagens.split(",")[0]}">
        <div class="serviceInfo">
            <h2 class="serviceName">${prestador.nome}</h2>
            <div id="separator">
                <p class="servicePrice">R$${prestador.preco}</p>
                <p class="serviceRating"></p>
            </div>    
            <p class="serviceDescription">${limitText(prestador.descricao, 213)}</p>
        </div>
    `;
    div.querySelector(".serviceRating").appendChild(createStarsElt(prestador.nota));
    div.addEventListener("click", ()=>{
        window.location.href = "./servico.html?id=" + prestador.ID;
    })
    return div;
}

function putPrestadoresList(prestadoresList){
    document.getElementById("servicesDiv").innerHTML = "";
    for(let i=0; i<prestadoresList.length; i++){
        if(prestadoresList[i].nome == "" || prestadoresList[i].preco == 0) continue;
        document.getElementById("servicesDiv").appendChild(createServiceDiv(prestadoresList[i]));
    }
}

function separate(imagens){
    let imgs = [];
    imgs = imagens.split(",");
    imgs.pop();
    return imgs;
}

function addResult(prestador){
    let div = document.createElement("div");
    div.classList.add("searchResult");
    div.innerHTML = `
        <img class="searchResultImg" src="${separate(prestador.imagens)[0]}">
        <span class="searchResultName">${prestador.nome}</span>
        <span class="searchResultPrice">R$${prestador.preco}</span>
    `;
    div.addEventListener("click", ()=>{
        window.location.href = "./servico.html?id=" + prestador.ID;
    })
    document.getElementById("searchResults").appendChild(div);
}

function addResults(prestadoresList){
    document.getElementById("searchResults").innerHTML = "";
    for(let i=0; i<prestadoresList.length; i++){
        if(prestadoresList[i].nome == "" || prestadoresList[i].preco == 0) continue;
        addResult(prestadoresList[i]);
    }
}

function putPrestadorBubble() {
    let login;
    try {
      login = JSON.parse(localStorage.getItem("login"));
    } catch {
      return;
    }
    if (login && login.type) {
      if (login.type == "prestador") {
        document.getElementById("bubbleDiv").style.display = "flex";
        document.getElementById("bubbleDiv").addEventListener("click", () => {
          window.location.href = "./prestador.html";
        });
      } else if (login.type == "consumidor") {
        document.getElementById("bubbleDiv").style.display = "flex";
        document.getElementById("bubbleDiv").addEventListener("click", () => {
          window.location.href = "./profile.html";
        });
      }
    }
  }


searchPrestadores("", -1, -1, 0, getPageCategory())
.then(data => {
    putPrestadoresList(data);
})

putPrestadorBubble();

document.getElementById("pesquisaIn").addEventListener("keyup", async e=>{
    e.preventDefault();
    document.getElementById("searchResults").style.opacity = "1";
    addResults((await searchPrestadores(document.getElementById("pesquisaIn").value, -1, -1, 0, -1)).slice(0, 3));
})

document.getElementById("pesquisaIn").addEventListener("focus", async e=>{
    e.preventDefault();
    document.getElementById("searchResults").style.opacity = "1";
    addResults((await searchPrestadores(document.getElementById("pesquisaIn").value, -1, -1, 0, -1)).slice(0, 3));
})

document.getElementById("pesquisaIn").addEventListener("blur", e=>{
    e.preventDefault();
    document.getElementById("searchResults").style.opacity = "0";
})

document.getElementById("pesquisaBtn").addEventListener("click", async e=>{
    e.preventDefault();
    let categoria = getPageCategory();


    currentResults = await searchPrestadores(document.getElementById("pesquisaIn").value, document.getElementById("priceMin").value, document.getElementById("priceMax").value, document.getElementById("orderType").value, categoria)
    putPrestadoresList(currentResults);
})

document.getElementById("searchBtn").addEventListener("click", async e=>{
    e.preventDefault();
    let categoria = getPageCategory();

    currentResults = await searchPrestadores(document.getElementById("pesquisaIn").value, document.getElementById("priceMin").value, document.getElementById("priceMax").value, document.getElementById("orderType").value, categoria)
    putPrestadoresList(currentResults);
})