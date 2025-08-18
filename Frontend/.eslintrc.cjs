module.exports = {
  env: {
    browser: true,
    es2021: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:react/recommended',
    'plugin:jsx-a11y/recommended',
    'prettier',
  ],
  plugins: ['react', 'jsx-a11y', 'prettier'],
  rules: {
    'prettier/prettier': [
      'error',
      {
        endOfLine: 'lf',
      },
    ],
  },
  settings: {
    react: {
      version: 'detect',
    },
  },
};
