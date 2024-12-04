const chrome = require('selenium-webdriver/chrome');
const { Builder, By, until, Key } = require('selenium-webdriver');
const { Actions } = require('selenium-webdriver/lib/input');


async function create_vehicle(driver, tentativas = 0) {
    tentativas++;
    const maxTentativas = 3;
    try {
         // campo do menu
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/header/div/button/span[2]")), 15000).click();
         await driver.sleep(1000);
        // Clica no menu lateral
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[1]/div/div[1]")), 15000).click();
        await driver.sleep(2000);
        // Seleciona cadastro
        await driver.findElement(By.css("#q-app > div > div.q-drawer-container > aside > div > div.q-list.q-mt-sm > div:nth-child(4) > div > div.q-item.q-item-type.row.no-wrap.q-item--clickable.q-link.cursor-pointer.q-focusable.q-hoverable > div.q-item__section.column.q-item__section--main.justify-center")).click();
        await driver.sleep(4000);
    //    Seleciona vehicle
        let vehicleElement = await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[2]/div[4]/div/div[2]/a[5]/div[3]/div")), 15000);
        await driver.sleep(2000);
        await driver.actions({ bridge: true }).move({ origin: vehicleElement }).click().perform();
        await driver.sleep(2000);
        // Clica no menu lateral
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/header/div/button/span[2]/i")), 15000).click();
        await driver.sleep(2000);
        //Adicionar
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[1]/button[1]/span[2]")), 15000).click();
        await driver.sleep(2000);
        // Modelo
        let modelo = await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div/div[1]/div/label/div/div/div/input")), 15000);

        // Adicionando foco no elemento
        await modelo.click(); // Clicar para garantir o foco
        await modelo.sendKeys(" "); // Enviar uma tecla (por exemplo, espaço) para garantir que o elemento está focado

        // Agora envie as teclas com o texto desejado
        await modelo.clear(); // Limpar o campo antes de enviar o texto, se necessário
        await driver.sleep(1000); // Aguardar um momento após limpar, se necessário
        await modelo.sendKeys("Fusca"); // Enviar o texto desejado
     

        // Clica na lista
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div/div[2]/div[1]/label/div/div[1]/div[1]/div")),15000).click();
       
        // Clica no primeiro item
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[4]/div/div[2]/div[1]")),15000).click();
       
        // Placa
        let placaInput = await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div/div[2]/div[2]/label/div/div[1]/div/input")), 15000);
        await placaInput.click(); 
        await driver.sleep(1000);
        await placaInput.sendKeys(""); 
        await placaInput.sendKeys("ABC1234");
   


        // Categotia
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div/div[3]/div/label/div/div[1]/div[1]/div")),15000).click();
        await driver.sleep(2000)
        // Seleciona a primeira opção
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[4]/div/div[2]/div[1]/div[2]/div")),15000).click();
        await driver.sleep(2000)
        // Cor
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div/div[4]/div/label")),15000).click();
        await driver.sleep(2000)
        // Seleciona cor
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[4]/div/div[2]/div[6]")),15000).click();

        // Capacidade (Kg)
        let capacidade = await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div/div[5]/div[1]/label/div/div/div/input")), 15000);
        await capacidade.click(); 
        await driver.sleep(1000);
        await capacidade.sendKeys(""); 
        await capacidade.sendKeys("2000");
       

        // Capacidade (Volume)
        let volume = await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div/div[5]/div[2]/label/div/div/div/input")), 15000);
        await volume.click(); 
        await driver.sleep(1000);
        await volume.sendKeys(""); 
        await volume.sendKeys("3000");

        await driver.sleep(2000);
       
        // Salvar
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[3]/div/button/span[2]/span")), 15000).click();
        
        await driver.sleep(4000);
      
        // Pesquisa
        let veicolo = await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div/div[1]/label/div/div/div[2]/input")), 15000);
        await veicolo.click(); 
        await driver.sleep(1000);
        await veicolo.sendKeys(""); 
        await veicolo.sendKeys("fusca");
        await driver.sleep(1000);
        //__________________________________________________

        // Seleciona no Fusca
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div/div[2]/table/tbody[2]/tr/td[7]/button/span[2]/i")), 15000).click();
        await driver.sleep(1000);
        // Clica em editar
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div/div[2]/div[2]/div")), 15000).click();
        
        // Capacidade (Kg)
        let capacidadeEd = await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div/div[2]/div[1]/label/div/div/div/input")), 15000);
        await capacidadeEd.click(); 
        await driver.sleep(1000);
        await capacidadeEd.sendKeys(""); 
        await capacidadeEd.sendKeys("2000");

        // Capacidade (Volume)
        let volume_ed = await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div/div[2]/div[2]/label/div/div/div/input")), 15000);
        await volume_ed.click(); 
        await driver.sleep(1000);
        await volume_ed.sendKeys(""); 
        await volume_ed.sendKeys("3000");
        
        // Seleciona o campo cor
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div/div[3]/div/label/div/div[1]/div[1]/div/div")), 15000).click();
        await driver.sleep(2000);
        // Seleciona uma cor
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[4]/div/div[2]/div[2]/div[2]")), 15000).click();
        await driver.sleep(2000);
        // salva
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[3]/button/span[2]/span")), 15000).click();

        //___________________________________________________
     
        // Seleciona no Fusca
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div/div[2]/table/tbody[2]/tr/td[7]/button/span[2]/i")), 15000).click();
        // Clica em excluir
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div/div[2]/div[2]/div")), 15000).click();
        // Ok
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[3]/button[2]/span[2]")), 15000).click();
        await driver.sleep(3000);
        return;

    } catch (error) {
        await driver.sleep(1000);
        console.error(`Ocorreu um erro durante a execução de create_vehicle (tentativa ${tentativas} de ${maxTentativas}):`, error);
        // Verifica se o número máximo de tentativas foi atingido
        if (tentativas === maxTentativas) {
            throw new Error("Número máximo de tentativas excedido ao criar o veiculo");
        } else {
            // Chama recursivamente a função com a próxima tentativa
            await create_vehicle(driver, tentativas);
        }
    }
}

module.exports = { create_vehicle};


