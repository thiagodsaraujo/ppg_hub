# PPG Hub Frontend

Frontend Neo-Brutalista para o sistema PPG Hub desenvolvido com React, TypeScript, e Vite.

## Características

- **Framework**: React 18 + TypeScript
- **Build Tool**: Vite
- **Estilo**: Tailwind CSS com tema Neo-Brutalista (inspirado no Gumroad)
- **Roteamento**: React Router v6
- **State Management**: Zustand
- **API Client**: Axios
- **Data Fetching**: TanStack Query (React Query)
- **Ícones**: Lucide React

## Tema Neo-Brutalista

O design segue o estilo Neo-Brutalista popularizado pelo Gumroad:

- Bordas pretas sólidas (2px)
- Sombras duras sem blur (4px 4px 0px #000)
- Cores vibrantes (rosa #ff90e8, amarelo #f1f333, ciano #90a8ed)
- Tipografia ousada (Inter font-weight 700-800)
- Efeitos de hover com movimento de sombra
- Componentes com alta contraste

## Estrutura do Projeto

```
frontend/
├── public/              # Arquivos estáticos
├── src/
│   ├── assets/          # Imagens, fontes, etc
│   ├── components/      # Componentes reutilizáveis
│   │   ├── ui/          # Componentes de UI (Button, Input, Card, Badge)
│   │   └── layout/      # Layouts (Auth, Dashboard)
│   ├── features/        # Features organizadas por domínio
│   │   └── auth/        # Feature de autenticação
│   ├── lib/             # Utilitários e configurações
│   ├── pages/           # Páginas da aplicação
│   ├── routes/          # Configuração de rotas
│   ├── stores/          # State management (Zustand)
│   ├── types/           # Tipos TypeScript globais
│   ├── App.tsx          # Componente principal
│   ├── main.tsx         # Entry point
│   └── index.css        # Estilos globais
├── index.html           # HTML template
├── package.json         # Dependências
├── vite.config.ts       # Configuração do Vite
├── tailwind.config.js   # Configuração do Tailwind
└── tsconfig.json        # Configuração do TypeScript
```

## Instalação

```bash
# Instalar dependências
npm install

# Criar arquivo .env a partir do exemplo
cp .env.example .env
```

## Configuração

Edite o arquivo `.env` com as configurações apropriadas:

```bash
VITE_API_URL=http://localhost:8000/api/v1
VITE_APP_NAME=PPG Hub
```

## Executando o Projeto

```bash
# Desenvolvimento (http://localhost:3000)
npm run dev

# Build para produção
npm run build

# Preview da build de produção
npm run preview

# Linter
npm run lint
```

## Backend

O frontend consome a API REST do PPG Hub que deve estar rodando em:
- **URL**: http://localhost:8000
- **API Base**: http://localhost:8000/api/v1
- **Total de endpoints**: 234

## Páginas Disponíveis

### Autenticação
- `/login` - Página de login
- `/register` - Página de registro

### Protegidas (requer autenticação)
- `/dashboard` - Dashboard do usuário
- `/` - Redireciona para dashboard

### Outras
- `*` - Página 404 (não encontrada)

## Componentes UI

### Button
```tsx
<Button variant="primary" isLoading={false}>
  Clique aqui
</Button>
```

Variantes: `primary`, `secondary`, `outline`

### Input
```tsx
<Input
  label="Email"
  type="email"
  placeholder="seu@email.com"
  error="Mensagem de erro (opcional)"
/>
```

### Card
```tsx
<Card className="p-4">
  Conteúdo do card
</Card>
```

### Badge
```tsx
<Badge variant="pink">Tag</Badge>
```

Variantes: `default`, `pink`, `yellow`, `cyan`

## Autenticação

O sistema usa autenticação JWT com tokens armazenados no localStorage via Zustand:

```typescript
// Hook do Zustand
const { user, token, login, logout, isAuthenticated } = useAuthStore();

// Login
login(accessToken, refreshToken, userData);

// Logout
logout();

// Verificar autenticação
if (isAuthenticated()) {
  // usuário está autenticado
}
```

## API Client

O cliente Axios está configurado em `src/lib/api.ts` com:
- Base URL configurável via env
- Interceptor para adicionar token JWT
- Interceptor para tratar erros 401 (redirect para login)

```typescript
import api from '@/lib/api';

// Fazer requisição
const response = await api.get('/endpoint');
const data = await api.post('/endpoint', { dados });
```

## Estilo e Design System

### Cores
```javascript
gum-black: '#000000'
gum-pink: '#ff90e8'
gum-yellow: '#f1f333'
gum-cyan: '#90a8ed'
gum-white: '#ffffff'
```

### Sombras
```javascript
shadow-brutal: '4px 4px 0px #000'
shadow-brutal-lg: '6px 6px 0px #000'
shadow-brutal-sm: '2px 2px 0px #000'
```

### Classes CSS Utilitárias
- `.btn-brutal` - Base para botões
- `.btn-primary` - Botão primário (rosa)
- `.btn-secondary` - Botão secundário (amarelo)
- `.btn-outline` - Botão outline (branco)
- `.card-brutal` - Card com borda e hover
- `.input-brutal` - Input estilizado
- `.badge-brutal` - Badge/pill

## Tecnologias

- **React** 18.2.0
- **TypeScript** 5.2.2
- **Vite** 5.0.8
- **Tailwind CSS** 3.4.0
- **React Router** 6.21.0
- **Axios** 1.6.2
- **Zustand** 4.4.7
- **TanStack Query** 5.14.0
- **Lucide React** 0.298.0
- **Zod** 3.22.4
- **React Hook Form** 7.49.0

## Licença

Este projeto faz parte do sistema PPG Hub.
