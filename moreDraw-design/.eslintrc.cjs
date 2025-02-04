module.exports = {
  // https://eslint.org/docs/user-guide/configuring#configuration-cascading-and-hierarchy
  // This option interrupts the configuration hierarchy at this file
  // Remove this if you have an higher level ESLint settings file (it usually happens into a monorepos)
  root: true,
  parserOptions: {
    parser: '@babel/eslint-parser',
    ecmaVersion: 2018, // Allows for the parsing of modern ECMAScript features
    sourceType: 'module' // Allows for the use of imports
  },
  env: {
    "browser": true,
    "commonjs": false,
    "es2021": true,
    "node": true,
    "jquery": true
  },
  // Rules order is important, please avoid shuffling them
  extends: [
    // Base ESLint recommended rules
    // 'eslint:recommended',
    // Uncomment any of the lines below to choose desired strictness,
    // but leave only one uncommented!
    // See https://eslint.vuejs.org/rules/#available-rules
    //'plugin:vue/vue3-essential', // Priority A: Essential (Error Prevention)
    // 'plugin:vue/vue3-strongly-recommended', // Priority B: Strongly Recommended (Improving Readability)
    'plugin:vue/vue3-recommended', // Priority C: Recommended (Minimizing Arbitrary Choices and Cognitive Overhead)
    'airbnb-base'
  ],
  plugins: [
    // https://eslint.vuejs.org/user-guide/#why-doesn-t-it-work-on-vue-files
    // required to lint *.vue files
    'vue',
  ],
  globals: {
    ga: 'readonly', // Google Analytics
    cordova: 'readonly',
    __statics: 'readonly',
    __QUASAR_SSR__: 'readonly',
    __QUASAR_SSR_SERVER__: 'readonly',
    __QUASAR_SSR_CLIENT__: 'readonly',
    __QUASAR_SSR_PWA__: 'readonly',
    process: 'readonly',
    Capacitor: 'readonly',
    chrome: 'readonly'
  },
  // add your custom rules here
  rules: {
    "linebreak-style" : "off",
    "prefer-destructuring" : "off",
    "vue/no-mutating-props" : "off",
    "vue/no-side-effects-in-computed-properties" : "off",
    "vue/require-default-prop": "off",
    "no-nested-ternary" : "off",
    "no-lonely-if" : "off",
    "import/no-unresolved" : "off",
    "import/prefer-default-export": "off",
    "vue/html-indent": [2, 4],
    "indent": ["error", 4, { "SwitchCase": 1 }],
    "no-use-before-define": "off",
    "no-plusplus": "off",
    "no-unused-vars": "off",
    "max-len": "off",
    "no-undef": "off",
    "no-restricted-syntax": "off",
    "no-restricted-globals": "off",
    "no-new": "off",
    "no-underscore-dangle": "off",
    "no-shadow": "off",
    "no-param-reassign": "off",
    "no-bitwise": "off",
    "no-loop-func": "off",
    "consistent-return": "off",
    "func-names": "off",
    "no-useless-escape": "off",
    "no-console": "off",
    "no-await-in-loop": "off",
    "object-curly-newline": "off",
    "new-cap": "off",
    "class-methods-use-this": "off",
    "import/no-cycle": "off",
    "import/extensions": "off",
    "import/no-extraneous-dependencies": "off",
    "import/no-useless-path-segments": "off",
    "vue/multi-word-component-names": "off",
    // allow debugger during development only
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off'
  }
}