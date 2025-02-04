export const about = {
    title: 'Detalles del Software',
    description: 'Este software es una aplicación completa para la gestión y seguimiento de envíos. Permite gestionar cargas unitarias, eventos de envíos, documentos y clientes, ofreciendo funcionalidades avanzadas de búsqueda e informes.',
    features: 'Características',
    featureList: 'Gestión de Cargas Unitarias, Eventos de Envíos, Documentos de Transporte, Informes Detallados de Clientes, Funcionalidades Avanzadas de Búsqueda, Integración con APIs para la obtención y limpieza de datos',
    architecture: 'Arquitectura',
    architectureDescription: 'El software está construido utilizando una arquitectura modular, donde diferentes componentes son responsables de diferentes partes del sistema. Los componentes principales son:',
    mainComponents: 'DataStorage: Gestiona el almacenamiento de datos de envíos, clientes, documentos y eventos. Entities: Definiciones de entidades como Shipment, Client, Doc, Unitload y ShipmentEvent. API: Capa responsable de la comunicación con servidores externos para la obtención y limpieza de datos.',
    technologiesUsed: 'Tecnologías Utilizadas',
    technologyList: 'Frontend: Vue.js con Quasar Framework, Backend: Java con Spring Boot, Comunicación: Axios para llamadas HTTP',
    installation: 'Instalación',
    installationSteps: 'Clone el repositorio del proyecto. Construya el backend utilizando Maven: mvn clean install. Inicie el backend: java -jar target/your-backend.jar. Inicie el frontend utilizando Quasar: quasar dev. Acceda a la aplicación vía navegador en http://localhost:9000',
    usage: 'Uso',
    usageDescription: 'Cargar datos de envíos y clientes, Buscar datos por fecha o tipo, Visualizar y limpiar datos cargados, Generar informes detallados',
};
