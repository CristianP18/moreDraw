const chrome = require('selenium-webdriver/chrome');
const { Builder, By, until, Key } = require('selenium-webdriver');
const { Actions } = require('selenium-webdriver/lib/input');
const { login } = require('../login');


async function create_boarding(driver, tentativas = 0) {
    tentativas++;
    const maxTentativas = 3;
    try {

        let elementPresent;

        try {
            // Verifica se o elemento tranportador está presente
            await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[2]/div[2]/div/div[1]/div[3]")), 15000);
            elementPresent = true; 
        } catch (error) {
            elementPresent = false; 
        }
        
        if (!elementPresent) {
        // campo do menu
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/header/div/button/span[2]")), 15000).click();
        await driver.sleep(1000);
       // Clica no menu lateral
       await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[1]/div/div[1]")), 15000).click();
        }
        // Clica no menu lateral
       await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[1]/div/div[1]")), 15000).click();
       // Seleciona transportador
       await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[2]/div[2]/div/div[1]/div[3]")), 15000).click();
       await driver.sleep(1000);
       // Seleciona Montar Carga
       await driver.findElement(By.css("a[href='/transporter/boarding'] div.q-item__label")).click();
        // Clica no menu lateral
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/header/div/button/span[2]/i")), 15000).click();
        // Adicionar carga
        await driver.findElement(By.xpath("//button[contains(@class, 'q-btn') and contains(@class, 'q-btn-item')]")).click();
       // Primeira carga
       await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[1]/div[3]/div/div[1]/div[1]/div[2]/button/span[2]/i")), 15000).click();
       await driver.sleep(1000)
    

    //..........................................//....................................................................
//     //Seleciona motorista aleátorio
//     const assignButton = await driver.findElement(By.xpath("//button[contains(@class, 'q-btn') and contains(@class, 'q-btn-item') and contains(@class, 'q-btn--unelevated') and contains(@class, 'q-btn--rectangle') and contains(@class, 'bg-primary') and contains(@class, 'text-white') and contains(@class, 'q-btn--actionable') and contains(@class, 'q-focusable') and contains(@class, 'q-hoverable') and contains(@class, 'q-mr-sm')]"));
//     await assignButton.click();
//     // Espera pela lista de motoristas aparecer
//     await driver.wait(until.elementLocated(By.css('.q-scrollarea')), 10000);

//     // Identifica todos os motoristas listados
//     const motoristas = await driver.findElements(By.css('.q-item'));
//     if (motoristas.length === 0) {
//         throw new Error("Nenhum motorista encontrado na lista");
//     }
// // Escolhe aleatoriamente um dos motoristas
// const indiceMotorista = Math.floor(Math.random() * motoristas.length);
// const motoristaSelecionado = motoristas[indiceMotorista];

// // Role a página para o motorista selecionado
// await driver.executeScript("arguments[0].scrollIntoView(true);", motoristaSelecionado);

// // Aguarde um curto período para garantir que o elemento seja totalmente exibido após a rolagem
// await driver.sleep(2000);

// // Localiza o botão de rádio dentro do motorista selecionado
// const botaoRadio = await motoristaSelecionado.findElement(By.css('.q-radio'));

// // Move para o botão de rádio e clica nele
// const actions = driver.actions({ bridge: true });
// await actions.move({ origin: botaoRadio }).click().perform();

// await driver.sleep(1000);


    //..............................................//................................................................


    //Seleciona motorista
    await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[2]/div[1]/button[6]/span[2]/span")), 15000).click();
    await driver.sleep(1000);
    // seleciona primeiro
    await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[3]/div[1]/div/div/div[1]/div[3]/div/div")), 15000).click();
    await driver.sleep(2000);
    //Selecione
    await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[4]/button[2]/span[2]/span")), 15000).click();

       //    // Formar carga
    //    await driver.findElement(By.xpath("//button[contains(@class, 'q-btn') and contains(@class, 'q-btn-item') and contains(@class, 'q-btn--unelevated') and contains(@class, 'q-btn--rectangle') and contains(@class, 'bg-primary') and contains(@class, 'text-white') and contains(@class, 'q-btn--actionable') and contains(@class, 'q-focusable') and contains(@class, 'q-hoverable') and contains(@class, 'q-mr-sm')]")).click();

    //    await driver.sleep(1000)
    //    // Seleciona o primeiro buton
    //    await driver.findElement(By.css("div.q-radio.cursor-pointer")).click();
    //    await driver.sleep(1000)
    //    // Seleciona 
    //    await driver.findElement(By.css("div.q-dialog__inner button.q-btn:nth-child(3)")).click();
    //    await driver.sleep(1000)
    //    //Clica em OK
    //    await driver.findElement(By.css("button.q-btn.q-btn-item[data-autofocus='true']")).click();
    // Três pontinhos
    await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[2]/main/div[2]/div[2]/div[1]/button[8]/span[2]/i")),10000).click()
    await driver.sleep(1000);
    // Excluir  ajustar aki!!
    await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div/div/div[2]/div")),10000).click();
       await driver.sleep(4000);
        return;
    } catch (error) {
        await driver.sleep(10000);
        console.error(`Ocorreu um erro durante a execução de create_boarding (tentativa ${tentativas} de ${maxTentativas}):`, error);
        // Verifica se o número máximo de tentativas foi atingido
        if (tentativas === maxTentativas) {
            throw new Error("Número máximo de tentativas excedido ao criar o embarque");
        } else {
            // Chama recursivamente a função com a próxima tentativa
            await login(driver);
            await create_boarding(driver, tentativas);
        }
    }
}

module.exports = { create_boarding };


