const { Builder, By, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const { Actions } = require('selenium-webdriver/lib/input');


async function create_unitloads(driver, tentativas = 0){
    const maxTentativas = 3;
    tentativas++;
    try{
         // Clica no menu lateral
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[1]/div/div[1]")), 10000).click();
         await driver.sleep(1000);
 
         // Seleciona Emitente
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[2]/div[1]/div/div[1]/div[3]")), 10000).click();
         await driver.sleep(1000);
 
 
         // Seleciona Emitente novamente
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[2]/div[1]/div/div[1]/div[3]")), 10000).click();
 
         // Seleciona Value
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[2]/div[1]/div/div[2]/a[1]/div[3]/div")), 10000).click();
 
         // Seleciona Adicionar
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/header/div/button/span[2]")), 10000).click();
         await driver.sleep(2000);
 
         // Clica em um botão qualquer, adaptar se necessário
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[1]/button[1]/span[2]")), 10000).click();
 
         // Gera código aleatório
         const codigo = generateRandomCode();
 
         // Seleciona nome e insere código gerado
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div[1]/div[1]/div/label/div/div[1]/div[1]/input")), 10000).sendKeys(codigo);
 
         // Clica e preenche categoria dos produtos
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div[1]/div[4]/div/label/div/div/div/input")), 10000).sendKeys("Teste");
 
         // Preenche outras informações conforme necessário
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div[1]/div[5]/div[1]/label/div/div/div/input")), 10000).sendKeys("4");
 
         // Preenche quantidade de volumes
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div[1]/div[5]/div[2]/label/div/div/div/input")), 10000).sendKeys("");
         await driver.sleep(4000);
         // Salva
         await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div[2]/button/span[2]/span")), 10000).click();
         await driver.sleep(6000);
         try{
            await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/form/div[2]/button/span[2]/span")), 10000).click();
         } catch{
            print("Erro ao cadastra Unitload");
         }
         
       
     return;
         
    } catch (erro){
        console.erro("Erro ao cadastrar unitloads")
        if (tentativas === maxTentativas) {
            throw new Error("Número máximo de tentativas excedido ao criar remessa");
        } else {
            // Chama recursivamente a função com a próxima tentativa
            await create_unitloads(driver, tentativas);
        }
    }
}

// Função para gerar código aleatório
function generateRandomCode() {
    const letras = [...Array(3)].map(() => String.fromCharCode(Math.floor(Math.random() * 26) + 65)).join('');
    const numeros = [...Array(9)].map(() => Math.floor(Math.random() * 10)).join('');
    return letras + numeros;
}

module.exports = {create_unitloads};