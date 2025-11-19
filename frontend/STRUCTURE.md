# Estrutura do Projeto Frontend

```
/home/user/ppg_hub/frontend/
├── 📄 .env                          # Variáveis de ambiente
├── 📄 .env.example                  # Exemplo de variáveis de ambiente
├── 📄 .gitignore                    # Arquivos ignorados pelo Git
├── 📄 README.md                     # Documentação principal
├── 📄 QUICKSTART.md                 # Guia rápido de instalação
├── 📄 STRUCTURE.md                  # Este arquivo (estrutura)
├── 📄 index.html                    # HTML template
├── 📄 package.json                  # Dependências e scripts
├── 📄 postcss.config.js             # Configuração PostCSS
├── 📄 tailwind.config.js            # Configuração Tailwind (tema Neo-Brutalista)
├── 📄 tsconfig.json                 # Configuração TypeScript
├── 📄 tsconfig.node.json            # TypeScript config para Node
├── 📄 vite.config.ts                # Configuração Vite
│
├── 📁 public/
│   └── 📄 vite.svg                  # Ícone do Vite
│
└── 📁 src/
    ├── 📄 App.tsx                   # Componente raiz
    ├── 📄 main.tsx                  # Entry point
    ├── 📄 index.css                 # CSS global (tema Neo-Brutalista)
    ├── 📄 vite-env.d.ts             # Types para Vite
    │
    ├── 📁 assets/                   # Imagens, fontes, etc
    │
    ├── 📁 components/
    │   ├── 📁 ui/                   # Componentes de UI reutilizáveis
    │   │   ├── 📄 Badge.tsx         # Badge/Tag Neo-Brutalista
    │   │   ├── 📄 Button.tsx        # Botão Neo-Brutalista
    │   │   ├── 📄 Card.tsx          # Card Neo-Brutalista
    │   │   └── 📄 Input.tsx         # Input Neo-Brutalista
    │   │
    │   └── 📁 layout/               # Layouts
    │       ├── 📄 AuthLayout.tsx    # Layout para páginas de auth
    │       └── 📄 DashboardLayout.tsx # Layout para dashboard
    │
    ├── 📁 features/                 # Features organizadas por domínio
    │   └── 📁 auth/                 # Feature de autenticação
    │       ├── 📁 components/
    │       │   ├── 📄 LoginForm.tsx      # Formulário de login
    │       │   └── 📄 RegisterForm.tsx   # Formulário de registro
    │       ├── 📁 services/
    │       │   └── 📄 authService.ts     # Service de autenticação
    │       └── 📁 types/
    │           └── 📄 auth.types.ts      # Types de autenticação
    │
    ├── 📁 lib/                      # Utilitários e configurações
    │   ├── 📄 api.ts                # Cliente Axios configurado
    │   └── 📄 queryClient.ts        # Cliente React Query
    │
    ├── 📁 pages/                    # Páginas da aplicação
    │   ├── 📄 DashboardPage.tsx     # Dashboard (protegida)
    │   ├── 📄 LoginPage.tsx         # Página de login
    │   ├── 📄 NotFoundPage.tsx      # Página 404
    │   └── 📄 RegisterPage.tsx      # Página de cadastro
    │
    ├── 📁 routes/                   # Configuração de rotas
    │   ├── 📄 AppRoutes.tsx         # Rotas da aplicação
    │   └── 📄 ProtectedRoute.tsx    # HOC para rotas protegidas
    │
    ├── 📁 stores/                   # State management (Zustand)
    │   └── 📄 authStore.ts          # Store de autenticação
    │
    └── 📁 types/                    # Types TypeScript globais
        └── 📄 index.ts              # Types compartilhados
```

## Resumo dos Arquivos

### Configuração (8 arquivos)
- `package.json` - Dependências e scripts
- `vite.config.ts` - Configuração do Vite (build tool)
- `tailwind.config.js` - Tema Neo-Brutalista (cores, sombras)
- `tsconfig.json` - TypeScript config
- `tsconfig.node.json` - TypeScript para Node
- `postcss.config.js` - PostCSS config
- `.env` - Variáveis de ambiente
- `.env.example` - Exemplo de env vars

### HTML/CSS (2 arquivos)
- `index.html` - Template HTML
- `src/index.css` - CSS global com tema Neo-Brutalista

### Core (4 arquivos)
- `src/App.tsx` - Componente raiz
- `src/main.tsx` - Entry point
- `src/lib/api.ts` - Cliente Axios
- `src/lib/queryClient.ts` - Cliente React Query

### Componentes UI (4 arquivos)
- `src/components/ui/Button.tsx` - Botão Neo-Brutalista
- `src/components/ui/Input.tsx` - Input Neo-Brutalista
- `src/components/ui/Card.tsx` - Card Neo-Brutalista
- `src/components/ui/Badge.tsx` - Badge Neo-Brutalista

### Layouts (2 arquivos)
- `src/components/layout/AuthLayout.tsx`
- `src/components/layout/DashboardLayout.tsx`

### Páginas (4 arquivos)
- `src/pages/LoginPage.tsx` - Login
- `src/pages/RegisterPage.tsx` - Cadastro
- `src/pages/DashboardPage.tsx` - Dashboard
- `src/pages/NotFoundPage.tsx` - 404

### Rotas (2 arquivos)
- `src/routes/AppRoutes.tsx` - Configuração de rotas
- `src/routes/ProtectedRoute.tsx` - HOC para proteção

### Auth Feature (5 arquivos)
- `src/features/auth/components/LoginForm.tsx`
- `src/features/auth/components/RegisterForm.tsx`
- `src/features/auth/services/authService.ts`
- `src/features/auth/types/auth.types.ts`
- `src/stores/authStore.ts` - Zustand store

### Types (2 arquivos)
- `src/types/index.ts` - Types globais
- `src/vite-env.d.ts` - Types do Vite

### Documentação (3 arquivos)
- `README.md` - Documentação completa
- `QUICKSTART.md` - Guia rápido
- `STRUCTURE.md` - Este arquivo

### Outros (2 arquivos)
- `.gitignore` - Git ignore
- `public/vite.svg` - Ícone

## Total: 37 arquivos criados

## Principais Tecnologias

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| React | 18.2.0 | Framework UI |
| TypeScript | 5.2.2 | Type safety |
| Vite | 5.0.8 | Build tool |
| Tailwind CSS | 3.4.0 | Estilização |
| React Router | 6.21.0 | Roteamento |
| Zustand | 4.4.7 | State management |
| Axios | 1.6.2 | HTTP client |
| TanStack Query | 5.14.0 | Data fetching |
| Lucide React | 0.298.0 | Ícones |

## Tema Neo-Brutalista

### Cores
- `gum-black`: #000000 (bordas e texto)
- `gum-pink`: #ff90e8 (primária)
- `gum-yellow`: #f1f333 (secundária)
- `gum-cyan`: #90a8ed (terciária)
- `gum-white`: #ffffff (fundo)

### Sombras (sem blur)
- `shadow-brutal`: 4px 4px 0px #000
- `shadow-brutal-lg`: 6px 6px 0px #000
- `shadow-brutal-sm`: 2px 2px 0px #000

### Tipografia
- Font: Inter (Google Fonts)
- Weights: 400, 500, 600, 700, 800
- Títulos: font-black (800)
- Botões/Labels: font-bold (700)

### Classes CSS Customizadas
- `.btn-brutal` - Base para botões
- `.btn-primary` - Botão rosa
- `.btn-secondary` - Botão amarelo
- `.btn-outline` - Botão branco
- `.card-brutal` - Card com borda
- `.input-brutal` - Input estilizado
- `.badge-brutal` - Badge/pill
