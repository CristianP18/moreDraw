const { Builder, By, Key, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const prompt = require('node-prompt');
const notifier = require('node-notifier');

async function create_client(driver) {
    try {
        // await delete_client();
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
        await driver.sleep(2000);

        // senha
        let senha = await driver.findElement(By.xpath("/html/body/div[4]/div/div[2]/div/div[2]/form/div/div/label/div/div/div[1]/input"));
        await senha.click();
        await driver.sleep(1000);
        await senha.sendKeys("teste123");
        await driver.sleep(1000);

        // enviar codigo de verificação
        await driver.findElement(By.xpath("/html/body/div[4]/div/div[2]/div/div[3]/form/div/div/div/button[2]/span[2]")).click();
        await driver.sleep(20000);
        //ok
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[5]/div/div[2]/div/div[2]/form/div/div/label/div/div/div")),10000).click();
        await driver.sleep(20000);
        // Clique para digitar
 

        // Preenche o campo de 'CEP'
        const cpf = await driver.wait(until.elementLocated(By.css("input[aria-label='CEP']")), 30000);
        await cpf.sendKeys('95320000');
        await driver.sleep(2000);
        // Preenche o campo de 'Bairro'
        const bairro = await driver.findElement(By.css("input[aria-label='Bairro']"));
        await bairro.sendKeys('Sagrada Familia');
        await driver.sleep(2000);
        // Preenche o campo de 'Endereço'
        const address = await driver.findElement(By.css("input[aria-label='Endereço']"));
        await address.sendKeys('Rua Antonio Polese'); // Substitua pelo CPF real do usuário
        await driver.sleep(2000);
        // Preenche o campo de 'Num'
        const number = await driver.findElement(By.css("input[aria-label='Nº']"));
        await number.sendKeys('123'); // Substitua pelo número real de celular do usuário
        await driver.sleep(2000);
        // Concluir
        await driver.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div/div[3]/button/span[2]")).click();
        await driver.sleep(8000);
        
    } catch (error) {
        console.error("Ocorreu um erro durante a criação do cliente:", error);
    }
}

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

module.exports = { create_client };
