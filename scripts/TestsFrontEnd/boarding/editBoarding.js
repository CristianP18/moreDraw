const chrome = require('selenium-webdriver/chrome');
const { Builder, By, until, Key } = require('selenium-webdriver');
const { Actions } = require('selenium-webdriver/lib/input');

// Função auxiliar para gerar um número aleatório no intervalo [min, max]
function gerarValorAleatorio(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min; // Retorna um valor inteiro
}

async function edit_boarding(driver, tentativas = 0) {
    tentativas++;
    const maxTentativas = 4;
    try {

                  // campo do menu
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/header/div/button/span[2]")), 15000).click();
        await driver.sleep(1000);
        
        
       // Clica no menu lateral
       await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[1]/div/div[1]")), 15000).click();
        
        await driver.sleep(1000);
        // Seleciona transportador
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[2]/div[2]/div/div[1]/div[3]")), 15000).click();
        // Editar Carga
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[2]/div[2]/div/div[2]/a[3]/div[3]/div")), 15000).click();
            
            // Seleciona carga
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[1]/div[2]/table/tbody[2]/tr[2]/td[3]")), 15000).click();
        await driver.sleep(1000);
        // Aqui ele seleciona a carga de forma aleatória
                    // Seleciona carga
   // Espera pela tabela estar visível
await driver.wait(until.elementLocated(By.css('.q-virtual-scroll__content')), 15000);
await driver.sleep(1000); // Dá um tempo para a tabela carregar completamente.

try {
    // Localiza todos os elementos na tabela que contêm a label 'Sim'.
    let editableItems = await driver.wait(until.elementsLocated(By.xpath("//div[contains(@class, 'q-virtual-scroll__content')]//div[contains(@class, 'q-chip--colored')]//div[text()='Sim']")), 15000);
    
    // Verifica se encontrou algum item editável.
    if (editableItems.length > 0) {
        // Clique no primeiro elemento editável encontrado.
        await editableItems[0].click();
        console.log("Primeiro item editável selecionado.");
    } else {
        console.log("Nenhum item editável encontrado.");
    }
} catch (error) {
    console.error("Ocorreu um erro ao tentar selecionar o primeiro item editável:", error);
}
     await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[1]/div[2]/table/tbody[2]/tr[2]/td[3]")), 15000).click();
        await driver.sleep(1000);
        // Aqui ele seleciona a carga de forma aleatória
   // Espera pela tabela estar visível
await driver.wait(until.elementLocated(By.css('.q-virtual-scroll__content')), 15000);
await driver.sleep(1000); // Dá um tempo para a tabela carregar completamente.

try {
    // Localize todos os elementos de linha (itens) dentro da tabela.
    let listItems = await driver.wait(until.elementsLocated(By.css('.q-virtual-scroll__content .q-tr')), 15000);
    
    // Verifica se encontrou algum item.
    if (listItems.length > 0) {
        for (let i = 0; i < listItems.length; i++) {
            // Aguarda e obtém o status editável (Sim/Não) de cada item.
            let statusChip = await listItems[i].findElement(By.css('.q-chip__content')).getText();
            
            if (statusChip === "Sim") {
                // Se o item for editável, clique nele e interrompa a iteração.
                await listItems[i].click();
                console.log("Item editável encontrado e selecionado.");
                break; // Sai do loop após encontrar e selecionar o primeiro item editável.
            }
            // Se o status for "Não", continua a iterar pelos itens seguintes.
        }
    } else {
        console.log("Nenhum item encontrado na lista.");
    }
} catch (error) {
    console.error("Ocorreu um erro ao procurar por um item editável:", error);
}






        // Clica em motorista
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[2]/div[2]/div[1]/label[2]/div/div[1]/div[1]/div[1]")), 15000).click();
        await driver.sleep(1000);
        // Espera até que a lista de itens esteja presente
        let listItems = await driver.wait(until.elementsLocated(By.css('.q-virtual-scroll__content .q-item--clickable')), 15000);

        // Verifica se há itens na lista
        if (listItems.length > 0) {
            // Gera um número aleatório com base no número de itens
            let randomIndex = Math.floor(Math.random() * listItems.length);
            
            // Clica no item aleatório da lista
            await listItems[randomIndex].click();
            await driver.sleep(2000);
        } else {
            console.log("Não foram encontrados itens clicáveis na lista.");
        }
         // Seleciona Veicolo
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[2]/div[2]/div[1]/label[3]/div")), 15000).click();
        // Seleciona um item da lista aleátorio
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div[1]")), 15000).click();
        await driver.sleep(2000);

         // de forma aleátoria
        // await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[2]/div[2]/div[1]/label[3]/div")), 15000).click();
        // // Seleciona um item da lista aleátorio
        // let listVehicles =  await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]")), 15000).click();
        // await driver.sleep(2000);
        //   // Verifica se há itens na lista
        //   if (listVehicles.length > 0) {
        //     // Gera um número aleatório com base no número de itens
        //     let randomIndex = Math.floor(Math.random() * listVehicles.length);
            
        //     // Clica no item aleatório da lista
        //     await listVehicles[randomIndex].click();
        //     await driver.sleep(2000);
        // } else {
        //     console.log("Não foram encontrados itens clicáveis na lista.");
        // }
        // Seleciona preço do frete:
        // Encontra e clica no campo de frete para ativá-lo
        let frete = await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[2]/div[2]/div[1]/label[4]/div")), 15000);
        await driver.actions({ bridge: true }).move({ origin: frete }).click().perform();
        await driver.sleep(2000); 


        // Pressiona e segura a tecla CONTROL (ou COMMAND no Mac), pressiona a tecla 'A' para selecionar tudo, solta as teclas CONTROL (ou COMMAND)
        await driver.actions({ bridge: true })
            .keyDown(Key.CONTROL) 
            .sendKeys('a')
            .keyUp(Key.CONTROL) 
            .perform();

        await driver.sleep(500); 

        // Pressiona a tecla DELETE para deletar o texto selecionado
        await driver.actions({ bridge: true })
            .sendKeys(Key.DELETE)
            .perform();
        
        // Gera um valor aleatório entre 15 e 300 para o frete e formata como valor em reais
        let valorFrete = gerarValorAleatorio(15, 300);
        let valorFreteFormatado = `R$ ${valorFrete.toFixed(2).replace('.', ',')}`; 
        
        // Simula a digitação do valor do frete no campo ativo
        for (let char of valorFreteFormatado) {
            await driver.actions({ bridge: true }).sendKeys(char).perform();
        }
        await driver.sleep(2000);
        
        let saveButton = await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div/button[2]/span[2]/span")), 10000);
        await saveButton.click();

      
        await driver.sleep(2000);
        // Adicinar remessa
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[3]/div[3]/button[2]/span[2]")),15000).click();
        // Seleciona primeiro item:
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[3]/div[1]/table/tbody[2]/tr[1]/td[1]/div/div")),15000).click();
        // Clica em Adicionar
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[4]/button[2]")),15000).click();
        // Seleciona remessa recem add
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[3]/div[2]/div[1]/div/div/div/table/tbody[2]/tr[2]/td[1]/div/div")),15000).click();
        await driver.sleep(2000);
        // Clica em remover
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[3]/div[3]/button[1]")),15000).click();
        await driver.sleep(1000);
        // Confirmar Alterações
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[1]/button[1]")), 10000).click();
        await driver.sleep(2000);
        // Ok
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[3]/button[2]")),15000).click();
        
        await driver.sleep(2000);

        return;
    } catch (error) {
        console.error(`Ocorreu um erro durante a execução de edit_boarding (tentativa ${tentativas} de ${maxTentativas}):`, error);
        if (tentativas >= maxTentativas) {
            throw new Error("Número máximo de tentativas excedido ao editar o embarque");
        } else {
            await driver.sleep(10000);
            await edit_boarding(driver, tentativas); 
        }
    }
    await driver.sleep(10000);
}

module.exports = {edit_boarding};


