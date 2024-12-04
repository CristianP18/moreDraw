const { Builder, By, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const { Actions } = require('selenium-webdriver/lib/input');

const { login } = require('./login'); 
const {adicionarRemessa} = require('./shipment/createShipments');
const {create_unitloads} = require('./unitload/createUnitloads');
const {create_boarding} = require('./boarding/createBoarding');
const {edit_boarding} = require('./boarding/editBoarding');
const { create_client_user } = require('./create_client/create_client_user');
const { create_client } = require('./create_client/create_client');
const { create_vehicle} = require('./vehicle/crateVehicle');


async function main() {
      // Configura as opções do Chrome para abrir em tela cheia
      let chromeOptions = new chrome.Options();
      chromeOptions.addArguments("--start-fullscreen");
        
        let driver = await new Builder()
        .forBrowser('chrome')
        .setChromeOptions(chromeOptions)
        .build();
    // Controle de Tests
    try {
        try {
            await login(driver);
        } catch (error) {
            console.error("Ocorreu um erro durante o login:", error);
        }
        await driver.sleep(2000);
    
         try {
             await create_unitloads(driver);
         } catch (error) {
             console.error("Ocorreu um erro durante a criação de unitloads:", error);
         }
         await driver.sleep(2000);
    
         try {
             await adicionarRemessa(driver);
         } catch (error) {
             console.error("Ocorreu um erro durante a adição de remessa:", error);
         }
         await driver.sleep(2000);
    
        //  try {
        //      await create_boarding(driver);
        //  } catch (error) {
        //      console.error("Ocorreu um erro durante a criação do boarding:", error);
        //  }
        //  await driver.sleep(2000);
    
        // try {
        //     await edit_boarding(driver);
        // } catch (error) {
        //     console.error("Ocorreu um erro durante a edição do boarding:", error);
        // }
        // await driver.sleep(4000);
    
        // try {
        //     await create_vehicle(driver);
        // } catch (error) {
        //     console.error("Ocorreu um erro durante a criação do veículo:", error);
        // }
        // await driver.sleep(2000);
    
        // try {
        //     await create_client_user(driver);
        // } catch (error) {
        //     console.error("Ocorreu um erro durante a criação do usuário cliente:", error);
        // }
        // await driver.sleep(2000);
    
        // try {
        //     await create_client(driver);
        // } catch (error) {
        //     console.error("Ocorreu um erro durante a criação do cliente:", error);
        // }
        // await driver.sleep(2000);
    
    } catch (error) {
        console.error("Ocorreu um erro inesperado durante a execução do script:", error);
    } finally {
        await driver.quit();
    }
    
}

async function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}


main();
