const { Builder, By, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const { Actions } = require('selenium-webdriver/lib/input');
async function login(driver) {
    try {
        await driver.get('http://moreDraw.dev.s3-website-sa-east-1.amazonaws.com/emitter/shipment');

        await driver.wait(until.elementLocated(By.css("input[aria-label='CNPJ']")), 10000).sendKeys("91.612.119/0001-70");
        await driver.wait(until.elementLocated(By.css("input[aria-label='Num. Celular']")), 10000).sendKeys("54999385264");
        await driver.wait(until.elementLocated(By.css("input[aria-label='Senha']")), 10000).sendKeys("teste123");

        // Clica no botão de login
        await driver.findElement(By.xpath("//div[contains(text(), 'Entrar')]/ancestor::button")).click();
        await driver.sleep(4000);

    } catch (error) {
        console.error("Ocorreu um erro durante o login:", error);
    }
}
module.exports = { login };