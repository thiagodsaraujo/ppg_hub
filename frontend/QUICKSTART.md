# Guia Rápido - PPG Hub Frontend

## Instalação e Execução

### 1. Instalar Dependências

```bash
cd /home/user/ppg_hub/frontend
npm install
```

### 2. Verificar Configuração

O arquivo `.env` já está configurado com:
```bash
VITE_API_URL=http://localhost:8000/api/v1
VITE_APP_NAME=PPG Hub
```

### 3. Executar o Frontend

```bash
npm run dev
```

O frontend estará disponível em: **http://localhost:3000**

### 4. Verificar Backend

Certifique-se de que o backend está rodando em:
```bash
http://localhost:8000
```

## Testando a Aplicação

### Login de Teste
1. Acesse: http://localhost:3000/login
2. Use credenciais de teste do backend
3. Ou crie uma nova conta em: http://localhost:3000/register

### Páginas Disponíveis
- `/login` - Login
- `/register` - Cadastro
- `/dashboard` - Dashboard (protegida)
- `/` - Redireciona para dashboard

## Comandos Disponíveis

```bash
npm run dev      # Desenvolvimento (http://localhost:3000)
npm run build    # Build para produção
npm run preview  # Preview da build
npm run lint     # Executar linter
```

## Estilo Neo-Brutalista

O design segue o estilo do Gumroad com:
- Bordas pretas sólidas (2px)
- Sombras duras (sem blur)
- Cores vibrantes (rosa, amarelo, ciano)
- Tipografia ousada (Inter)
- Efeitos de hover interativos

## Tecnologias

- React 18 + TypeScript
- Vite (build tool)
- Tailwind CSS (estilização)
- React Router (rotas)
- Zustand (state)
- Axios (API)
- TanStack Query (data fetching)
- Lucide React (ícones)

## Estrutura de Pastas

```
frontend/
├── src/
│   ├── components/    # Componentes reutilizáveis
│   ├── features/      # Features por domínio
│   ├── pages/         # Páginas
│   ├── routes/        # Rotas
│   ├── stores/        # State management
│   ├── lib/           # Utilitários
│   └── types/         # TypeScript types
├── public/            # Arquivos estáticos
└── index.html         # HTML template
```

## Troubleshooting

### Erro de conexão com API
- Verifique se o backend está rodando em http://localhost:8000
- Verifique o arquivo `.env` se a URL da API está correta

### Erro ao instalar dependências
```bash
rm -rf node_modules package-lock.json
npm install
```

### Porta 3000 já em uso
Edite `vite.config.ts` e altere a porta:
```typescript
server: {
  port: 3001, // ou outra porta disponível
}
```
