/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        gum: {
          black: '#000000',
          pink: '#ff90e8',
          yellow: '#f1f333',
          cyan: '#90a8ed',
          white: '#ffffff',
          gray: {
            50: '#f8f8f8',
            100: '#f0f0f0',
            200: '#e0e0e0',
            300: '#c0c0c0',
          },
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        brutal: '4px 4px 0px #000',
        'brutal-lg': '6px 6px 0px #000',
        'brutal-sm': '2px 2px 0px #000',
      },
      borderWidth: {
        DEFAULT: '2px',
        3: '3px',
      },
    },
  },
  plugins: [],
}
