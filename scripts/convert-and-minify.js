const fs = require('fs');
const yaml = require('js-yaml');
const jsonminify = require('jsonminify');

// Verifica se os argumentos de linha de comando foram fornecidos corretamente
if (process.argv.length !== 4) {
    console.error('Usage: node convert-and-minify.js <input_yaml_file> <output_json_file>');
    process.exit(1);
}

// Extrai o caminho do arquivo YAML de entrada e o caminho do arquivo JSON de saída dos argumentos de linha de comando
const inputYamlFile = process.argv[2];
const outputJsonFile = process.argv[3];

try {
    // Lê o conteúdo do arquivo YAML
    const yamlContent = fs.readFileSync(inputYamlFile, 'utf8');

    // Converte o conteúdo YAML em objeto JavaScript
    const jsonObject = yaml.load(yamlContent);

    // Converte o objeto JavaScript em uma string JSON e minifica
    const jsonString = jsonminify(JSON.stringify(jsonObject));

    // Escreve a string JSON minificada no arquivo de saída
    fs.writeFileSync(outputJsonFile, jsonString, 'utf8');

    console.log(`Converted and minified YAML to JSON: ${inputYamlFile} -> ${outputJsonFile}`);
} catch (error) {
    console.error('Error:', error.message);
    process.exit(1);
}


//const fs = require('fs');
//const yaml = require('js-yaml');
//
//// Verifica se os argumentos de linha de comando foram fornecidos corretamente
//if (process.argv.length !== 4) {
//    console.error('Usage: node convert-and-minify.js <input_yaml_file> <output_json_file>');
//    process.exit(1);
//}
//
//// Extrai o caminho do arquivo YAML de entrada e o caminho do arquivo JSON de saída dos argumentos de linha de comando
//const inputYamlFile = process.argv[2];
//const outputJsonFile = process.argv[3];
//
//try {
//    // Lê o conteúdo do arquivo YAML
//    const yamlContent = fs.readFileSync(inputYamlFile, 'utf8');
//
//    // Converte o conteúdo YAML em objeto JavaScript
//    const jsonObject = yaml.load(yamlContent);
//
//    // Converte o objeto JavaScript em uma string JSON
//    const jsonString = JSON.stringify(jsonObject, null, 2); // A opção 'null, 2' é para formatar a saída com indentação de 2 espaços
//
//    // Escreve a string JSON no arquivo de saída
//    fs.writeFileSync(outputJsonFile, jsonString, 'utf8');
//
//    console.log(`Converted and minified YAML to JSON: ${inputYamlFile} -> ${outputJsonFile}`);
//} catch (error) {
//    console.error('Error:', error.message);
//    process.exit(1);
//}
