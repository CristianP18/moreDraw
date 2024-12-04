const { Builder, By, Key, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const { Actions } = require('selenium-webdriver/lib/input');

async function create_client_user(driver) {
    try {
        delete_client();
    } catch (error) {
        console.error("Ocorreu um erro durante a requisição rest delete_cliente:", error);
    }
    try {
        await driver.get('http://moreDraw.dev.s3-website-sa-east-1.amazonaws.com');

        // Clica no botão para abrir o formulário de cadastro
        let signUpButton = await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/div[1]/div/div[2]/div[1]/button")), 15000);
        await signUpButton.click();
        await driver.sleep(1000);

        // Razão social
        let social = await driver.wait(until.elementLocated(By.xpath("//input[@aria-label='Razão Social da Empresa *']")), 15000);
        await social.sendKeys("Razão Social");
        await driver.sleep(1000);

        // CNPJ
        let cnpjField = await driver.wait(until.elementLocated(By.xpath("//input[@aria-label='CNPJ da Empresa *']")), 10000);
        await cnpjField.sendKeys("96278173000145");
        await driver.sleep(1000);

        // Celular
        let phoneField = await driver.wait(until.elementLocated(By.xpath("//input[@aria-label='Num. Celular *']")), 10000);
        await phoneField.sendKeys("54996227442");
        await driver.sleep(1000);

        // CPF
        let passwordField = await driver.wait(until.elementLocated(By.xpath("//input[@aria-label='Insira seu CPF *']")), 10000);
        await passwordField.sendKeys("02710983095");
        await driver.sleep(2000);

        // E-mail
        let email = await driver.wait(until.elementLocated(By.xpath("//input[@aria-label='E-mail da Empresa *']")), 10000);
        await email.sendKeys("sparktest@gmail.com");
        await driver.sleep(2000);

        // Clicar no botão de envio do formulário
        await driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div/div[3]/button")).click();
        await driver.sleep(4000);

      
        // Nome
          // Espera até que o elemento seja carregado e visível
          const nomeField = await driver.wait(until.elementLocated(By.id('f_c3bcaf3c-5e37-4f6b-835f-09c622e9a4aa')), 10000);

          // Cria uma instância de Actions
          const actions = driver.actions({bridge: true});
  
          // Move o cursor até o elemento e clica
          await actions.move({origin: nomeField}).click().perform();

        // Simula um clique no elemento
        await elemento.click();
        
        // Aguarda um tempo para a ação ser completada (opcional)
        await driver.sleep(2000); 
                
        
        
        
        await nome.sendKeys("ClienteTeste");
        await driver.sleep(2000);
        // Sobre Nome
        let sobreNome = await driver.wait(until.elementLocated(By.xpath("//input[@aria-label='Sobrenome']")), 10000);
        await sobreNome.sendKeys("TestAutomatizado");
        await driver.sleep(2000);

   

        // Confirmar senha
        let confirmarSenha = await driver.wait(until.elementLocated(By.xpath("//input[@aria-label='Confirmar senha']")), 10000);
        await confirmarSenha.sendKeys("teste123");
        await driver.sleep(2000);

        // Clicar no botão de finalização do cadastro
        await driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div/div[3]/button[2]")).click();
        await driver.sleep(8000);

        // Aqui pode seguir o script, se houver mais passos a realizar

    } catch (error) {
        console.error("Ocorreu um erro durante a criação do cliente:", error);
    }
}

module.exports = { create_client_user };


async function delete_client(){
    const fetch = require('node-fetch');
    try {
        const response = await fetch('https://api.moreDraw.com.br/dev/rest/client/test', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'x-api-key': 'yb9qulxRUY2G3lL2wdXWI4HgWSQXjYZW4fwt3jX3'
            }
        });
        if (!response.ok) {
            throw new Error('Network response was not ok ' + response.statusText);
        }
        const data = await response.json();
        console.log(data);
    } catch (error) {
        console.error('There was a problem with your fetch operation:', error);
    }
}