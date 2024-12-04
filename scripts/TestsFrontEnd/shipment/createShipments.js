const { Builder, By, until, Key } = require('selenium-webdriver');
const { Actions } = require('selenium-webdriver/lib/input');

async function adicionarRemessa(driver, tentativas = 0) {
    const maxTentativas = 3;
    tentativas++;
    try {
        // Clica no menu lateral
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[1]/div/div[1]")), 15000).click();
        await driver.sleep(1000);
        // Seleciona Emitente
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[2]/div[1]/div/div[1]/div[3]")), 15000).click();
        await driver.sleep(1000);
        // Seleciona Remessas
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/div[1]/aside/div/div[2]/div[1]/div/div[2]/a[2]/div[3]")), 15000).click();
        await driver.sleep(1000);
        // campo do menu
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/header/div/button/span[2]")), 15000).click();
        await driver.sleep(1000);

        // Adicionar Remessa
        await driver.wait(until.elementLocated(By.xpath("//button[contains(@class, 'q-btn--actionable') and contains(., 'Adicionar')]")), 15000).click();

        await driver.sleep(3000);

        // Clica no checkbox de transporte próprio
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/div[2]/div/div/div/div/div/div[1]/div/div[1]/div/div[1]/form/div[1]/div[1]/div/div[1]/div/i")), 15000).click();

        // Gera um nome aleatório
        const nomes = ["Lucas", "Pedro", "Joao", "Rafael", "Cristiano"];
        const nomeAleatorio = nomes[Math.floor(Math.random() * nomes.length)];

        // Seleciona o campo de nome e insere o nome aleatório
        const campoNome = await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/div[2]/div/div/div/div/div/div[1]/div/div[1]/div/div[1]/form/div[1]/div[6]/div[1]/label/div[1]/div/div/div/input")), 10000);
        await campoNome.click();
        await campoNome.sendKeys(nomeAleatorio);

        // Seleciona a primeira opção disponível
        await driver.wait(until.elementLocated(By.css(".q-item.q-item-type.row.no-wrap.q-item--clickable.q-link.cursor-pointer.q-manual-focusable")), 15000).then(element => element.click());

        await driver.sleep(2000);
        // Seleciona trocar endereço
        await driver.findElement(By.css("body > div:nth-child(6) > div > div.q-dialog__inner.flex.no-pointer-events.q-dialog__inner--minimized.q-dialog__inner--standard.fixed-full.flex-center > div > div.q-stepper.q-stepper--horizontal > div.q-stepper__content.q-panel-parent > div > div > div > div > div > div:nth-child(1) > div > div.q-scrollarea__container.scroll.relative-position.fit.hide-scrollbar > div > div.q-card__section.q-card__section--vert.q-pa-lg > form > div:nth-child(2) > div > div > div > div.q-expansion-item__content.relative-position > div > form > div > div > div > div:nth-child(5) > div:nth-child(2) > div > div.q-toggle__label.q-anchor--skip")).click();
            
        // Gera um número aleatório de três dígitos
        const numeroAleatorio = Math.floor(Math.random() * (999 - 100) + 100).toString();
        
        // Seleciona o campo de número e insere o número aleatório
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/div[2]/div/div/div/div/div/div[1]/div/div[1]/div/div[1]/form/div[2]/div/div/div/div[2]/div/form/div/div/div/div[1]/div[2]/label/div/div[1]")), 2000).click();

        await driver.actions().sendKeys(Key.NUMPAD1, Key.NUMPAD2, Key.NUMPAD3).perform();

        // Seleciona a cidade
        const campoCidade =  await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/div[2]/div/div/div/div/div/div[1]/div/div[1]/div/div[1]/form/div[2]/div/div/div/div[2]/div/form/div/div/div/div[2]/div[1]/label/div/div[1]/div/input")), 10000);
        await campoCidade.click();
        
        // await driver.sleep(4000);

        // Limpa o campo de cidade
        await campoCidade.clear();

        // Escolhe uma cidade aleatória
        const cidades = ["Porto Alegre", "Gramado", "Caxias do Sul", "Pelotas", "Novo Hamburgo", "Bento Gonçalves", "Passo Fundo", "Santa Maria", "São Leopoldo", "Canoas", "Florianópolis", "Joinville", "Blumenau", "Balneário Camboriú", "Chapecó", "Criciúma", "Itajaí", "Jaraguá do Sul", "Lages", "São José", "São Paulo", "Campinas", "Santos", "Ribeirão Preto", "Sorocaba", "São José dos Campos", "Osasco", "Santo André", "São Bernardo do Campo", "Jundiaí"];
        const cidadeAleatoria = cidades[Math.floor(Math.random() * cidades.length)];
        await campoCidade.clear();
        await campoCidade.sendKeys(cidadeAleatoria);

        // Finaliza a operação
        const botaoFinalizar =  await driver.findElement(By.css("body > div:nth-child(6) > div > div.q-dialog__inner.flex.no-pointer-events.q-dialog__inner--minimized.q-dialog__inner--standard.fixed-full.flex-center > div > div.q-stepper.q-stepper--horizontal > div.q-stepper__content.q-panel-parent > div > div > div > div > div > div:nth-child(1) > div > div.q-scrollarea__container.scroll.relative-position.fit.hide-scrollbar > div > div.q-card__actions.justify-start.q-card__actions--horiz.row.q-px-lg > button > span.q-btn__content.text-center.col.items-center.q-anchor--skip.justify-center.row"), 10000);
        await botaoFinalizar.click();

        // Gera um número aleatório de 10 dígitos
        let numAleatorio10 = Math.floor(Math.random() * 9000000000 + 1000000000).toString();

        // Concatena o número 4 vezes
        let codigoBase = numAleatorio10 + numAleatorio10 + numAleatorio10 + numAleatorio10;

        // Gera dois dígitos aleatórios adicionais
        let digitosAdicionais = Math.floor(Math.random() * 100).toString().padStart(4, '0');

        // Concatena os dois dígitos ao código base para formar o código final de 44 dígitos
        let codigoBarras = codigoBase + digitosAdicionais;

        await driver.wait(until.elementLocated(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/div[2]/div/div/div/div/div/div[1]/div[1]/form/div/div[5]/div/label/div/div/div/input")), 10000).sendKeys(codigoBarras);
        await driver.sleep(1000);
        //Abre a caixa de seleção de unitloads
        await driver.findElement(By.css("button.q-btn.q-btn-item.non-selectable.no-outline.q-btn--unelevated.q-btn--rectangle.bg-primary.text-white.q-btn--actionable.q-focusable.q-hoverable.q-btn--dense > span.q-btn__content.text-center.col.items-center.q-anchor--skip.justify-center.row > i.q-icon.mdi.mdi-plus")).click();
        
        // Add unitloads
        const addUnitLoadButton = await driver.wait(until.elementLocated(By.css("body > div:nth-child(7) > div > div.q-dialog__inner.flex.no-pointer-events.q-dialog__inner--minimized.q-dialog__inner--standard.fixed-full.flex-center > div > div.q-table__container.q-table--horizontal-separator.column.no-wrap.q-table__card.q-table--flat.q-table--dense.q-table--no-wrap.my-sticky-virtscroll-table > div.q-table__middle.q-virtual-scroll.q-virtual-scroll--vertical.scroll > table > tbody.q-virtual-scroll__content > tr:nth-child(1) > td.q-table--col-auto-width > div > div > div > svg")), 10000);
        await addUnitLoadButton.click();

        await driver.sleep(2000);

        // Clica no botão adcionar
        await driver.findElement(By.xpath('/html/body/div[4]/div/div[2]/div/div[4]/button[2]/span[2]')).click();

        await driver.sleep(2000);
        
        // Botão para não imprimir e clica
        const noPrintButtonSelector = "body > div:nth-child(6) > div > div.q-dialog__inner.flex.no-pointer-events.q-dialog__inner--minimized.q-dialog__inner--standard.fixed-full.flex-center > div > div.q-stepper.q-stepper--horizontal > div.q-stepper__content.q-panel-parent > div > div > div > div > div > div:nth-child(1) > div.q-card__actions.justify-start.q-card__actions--horiz.row.q-px-lg.q-mb-sm > div.q-checkbox.cursor-pointer.no-outline.row.inline.no-wrap.items-center.q-mr-sm > div.q-checkbox__label.q-anchor--skip";
        const noPrintButton = await driver.wait(until.elementLocated(By.css(noPrintButtonSelector)), 10000);
        await noPrintButton.click();
        await driver.sleep(1000);
        // Finaliza a operação clicando no botão finalizar
        const finalizeButtonSelector = "body > div:nth-child(6) > div > div.q-dialog__inner.flex.no-pointer-events.q-dialog__inner--minimized.q-dialog__inner--standard.fixed-full.flex-center > div > div.q-stepper.q-stepper--horizontal > div.q-stepper__content.q-panel-parent > div > div > div > div > div > div:nth-child(1) > div.q-card__actions.justify-start.q-card__actions--horiz.row.q-px-lg.q-mb-sm > button.q-btn.q-btn-item.non-selectable.no-outline.q-btn--unelevated.q-btn--rectangle.bg-primary.text-white.q-btn--actionable.q-focusable.q-hoverable.q-mr-sm > span.q-btn__content.text-center.col.items-center.q-anchor--skip.justify-center.row";
        const finalizeButton = await driver.wait(until.elementLocated(By.css(finalizeButtonSelector)), 10000);
        await finalizeButton.click();
        await driver.sleep(1000);

        // Clica no botão para voltar ou fechar, se necessário
        const backButton = await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/header/div/button/span[2]/i")), 10000);
        await backButton.click();
        
        await driver.sleep(2000);
        // Menu
        await driver.wait(until.elementLocated(By.xpath("/html/body/div[1]/div/header/div/button/span[2]/i")), 15000);
        return;
    } catch (error) {
        await driver.sleep(10000);
        console.error(`Ocorreu um erro durante a execução de createShipment (tentativa ${tentativas} de ${maxTentativas}):`, error);
        // Verifica se o número máximo de tentativas foi atingido
        if (tentativas === maxTentativas) {
            throw new Error("Número máximo de tentativas excedido ao criar remessa");
        } else {
            // Chama recursivamente a função com a próxima tentativa
            await create_boarding(driver, tentativas);
        }
    }
}    
    
module.exports = {adicionarRemessa};