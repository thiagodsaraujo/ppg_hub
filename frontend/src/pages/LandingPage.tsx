import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import {
  GraduationCap,
  Users,
  BookOpen,
  BarChart3,
  Award,
  CheckCircle2,
  ArrowRight,
  Github,
  Mail
} from 'lucide-react';

export const LandingPage = () => {
  const navigate = useNavigate();

  const features = [
    {
      icon: Users,
      title: 'Gestão de Usuários',
      description: 'Controle completo de docentes, discentes e equipe administrativa',
      color: 'bg-gum-pink'
    },
    {
      icon: BookOpen,
      title: 'Disciplinas e Matrículas',
      description: 'Sistema integrado para ofertas de disciplinas e matrículas online',
      color: 'bg-gum-yellow'
    },
    {
      icon: GraduationCap,
      title: 'Trabalhos e Bancas',
      description: 'Acompanhamento de TCCs, dissertações e teses com agendamento de bancas',
      color: 'bg-gum-cyan'
    },
    {
      icon: BarChart3,
      title: 'Relatórios e Dashboards',
      description: 'Estatísticas completas e relatórios em PDF, Excel e CSV',
      color: 'bg-gum-pink'
    },
    {
      icon: Award,
      title: 'Integração OpenAlex',
      description: 'Sincronização automática de métricas acadêmicas (H-index, publicações)',
      color: 'bg-gum-yellow'
    },
  ];

  const stats = [
    { value: '234', label: 'Endpoints REST' },
    { value: '100%', label: 'Clean Architecture' },
    { value: '3', label: 'Schemas PostgreSQL' },
    { value: '19', label: 'Entidades' },
  ];

  return (
    <div className="min-h-screen bg-gum-white">
      {/* Header/Navbar */}
      <header className="border-b-2 border-gum-black bg-gum-white sticky top-0 z-50">
        <div className="container mx-auto px-4 py-4">
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-gum-pink border-2 border-gum-black rounded-md flex items-center justify-center">
                <GraduationCap size={28} strokeWidth={2.5} />
              </div>
              <div>
                <h1 className="text-2xl font-black">PPG Hub</h1>
                <p className="text-xs font-bold text-gray-600">Sistema de Gestão para Pós-Graduação</p>
              </div>
            </div>

            <div className="flex gap-3">
              <Button variant="outline" onClick={() => navigate('/login')}>
                Entrar
              </Button>
              <Button variant="primary" onClick={() => navigate('/register')}>
                Cadastrar
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="py-20 px-4 bg-gradient-to-br from-gum-pink via-gum-yellow to-gum-cyan">
        <div className="container mx-auto max-w-6xl">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div className="space-y-6">
              <div className="inline-flex items-center gap-2 px-4 py-2 bg-gum-white border-2 border-gum-black rounded-full">
                <CheckCircle2 size={18} />
                <span className="font-bold text-sm">100% Open Source</span>
              </div>

              <h2 className="text-5xl md:text-6xl font-black leading-tight">
                Gestão Moderna para
                <span className="block text-transparent bg-clip-text bg-gradient-to-r from-purple-600 to-pink-600">
                  Programas de Pós-Graduação
                </span>
              </h2>

              <p className="text-xl font-medium text-gray-700 leading-relaxed">
                Sistema completo com <strong>234 endpoints REST</strong>, autenticação JWT,
                integração OpenAlex e relatórios automáticos. Desenvolvido com
                <strong> Spring Boot</strong> + <strong>React</strong>.
              </p>

              <div className="flex flex-wrap gap-4">
                <Button
                  variant="primary"
                  onClick={() => navigate('/register')}
                  className="text-lg px-8 py-4"
                >
                  Começar Agora
                  <ArrowRight size={20} />
                </Button>

                <Button
                  variant="outline"
                  onClick={() => window.open('http://localhost:8000/swagger-ui.html', '_blank')}
                  className="text-lg px-8 py-4"
                >
                  Ver API Docs
                </Button>
              </div>
            </div>

            {/* Hero Card */}
            <Card className="p-8 shadow-brutal-lg">
              <div className="space-y-6">
                <div className="flex items-center justify-between">
                  <h3 className="text-2xl font-black">Status do Sistema</h3>
                  <span className="flex items-center gap-2 px-3 py-1 bg-green-100 border-2 border-green-500 rounded-full">
                    <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
                    <span className="font-bold text-sm text-green-700">Online</span>
                  </span>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  {stats.map((stat, index) => (
                    <div
                      key={index}
                      className="p-4 bg-gradient-to-br from-gum-gray-50 to-white border-2 border-gum-black rounded-md"
                    >
                      <div className="text-3xl font-black">{stat.value}</div>
                      <div className="text-sm font-bold text-gray-600">{stat.label}</div>
                    </div>
                  ))}
                </div>

                <div className="pt-4 border-t-2 border-gum-black">
                  <p className="text-sm font-bold text-gray-600 mb-2">Tecnologias</p>
                  <div className="flex flex-wrap gap-2">
                    {['Java 17', 'Spring Boot', 'React 18', 'PostgreSQL', 'Docker'].map((tech) => (
                      <span
                        key={tech}
                        className="px-3 py-1 bg-gum-cyan border-2 border-gum-black rounded-full font-bold text-sm"
                      >
                        {tech}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            </Card>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 px-4 bg-gum-white">
        <div className="container mx-auto max-w-6xl">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-black mb-4">
              Recursos Poderosos
            </h2>
            <p className="text-xl font-medium text-gray-600 max-w-2xl mx-auto">
              Tudo que você precisa para gerenciar seu programa de pós-graduação
              de forma eficiente e moderna
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {features.map((feature, index) => {
              const Icon = feature.icon;
              return (
                <Card key={index} className="p-6 hover:shadow-brutal-lg transition-all">
                  <div className={`w-14 h-14 ${feature.color} border-2 border-gum-black rounded-md flex items-center justify-center mb-4`}>
                    <Icon size={28} strokeWidth={2.5} />
                  </div>
                  <h3 className="text-xl font-black mb-2">{feature.title}</h3>
                  <p className="font-medium text-gray-600">{feature.description}</p>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* Architecture Section */}
      <section className="py-20 px-4 bg-gradient-to-r from-gum-cyan to-gum-pink">
        <div className="container mx-auto max-w-6xl">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div>
              <h2 className="text-4xl md:text-5xl font-black mb-6">
                Arquitetura de Classe Mundial
              </h2>
              <div className="space-y-4">
                {[
                  'Clean Architecture em 4 camadas',
                  'SOLID Principles aplicados',
                  'RESTful API completa',
                  'Segurança JWT robusta',
                  'Lock Pessimista para concorrência',
                  'Views Materializadas otimizadas',
                  'CI/CD com GitHub Actions',
                  'Docker Compose pronto para produção'
                ].map((item, index) => (
                  <div key={index} className="flex items-center gap-3">
                    <CheckCircle2 size={24} className="text-gum-black flex-shrink-0" />
                    <span className="font-bold text-lg">{item}</span>
                  </div>
                ))}
              </div>
            </div>

            <Card className="p-8 bg-gum-white">
              <h3 className="text-2xl font-black mb-6">Módulos Implementados</h3>
              <div className="space-y-3">
                {[
                  { name: 'CORE', endpoints: 40, status: 'Completo' },
                  { name: 'AUTH', endpoints: 38, status: 'Completo' },
                  { name: 'ACADEMIC', endpoints: 139, status: 'Completo' },
                  { name: 'INTEGRATIONS', endpoints: 11, status: 'Completo' },
                  { name: 'MONITORING', endpoints: 6, status: 'Completo' },
                ].map((module, index) => (
                  <div
                    key={index}
                    className="flex items-center justify-between p-3 bg-gum-gray-50 border-2 border-gum-black rounded-md"
                  >
                    <div className="flex items-center gap-3">
                      <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                      <span className="font-black">{module.name}</span>
                    </div>
                    <div className="text-right">
                      <div className="font-bold text-sm text-gray-600">{module.endpoints} endpoints</div>
                      <div className="text-xs font-bold text-green-600">{module.status}</div>
                    </div>
                  </div>
                ))}
              </div>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 px-4 bg-gum-black text-gum-white">
        <div className="container mx-auto max-w-4xl text-center">
          <h2 className="text-4xl md:text-5xl font-black mb-6">
            Pronto para Começar?
          </h2>
          <p className="text-xl font-medium mb-8 text-gray-300">
            Crie sua conta gratuitamente e comece a gerenciar seu programa de pós-graduação hoje mesmo.
          </p>
          <div className="flex flex-wrap gap-4 justify-center">
            <Button
              variant="primary"
              onClick={() => navigate('/register')}
              className="text-lg px-8 py-4"
            >
              Criar Conta Grátis
              <ArrowRight size={20} />
            </Button>
            <Button
              variant="secondary"
              onClick={() => navigate('/login')}
              className="text-lg px-8 py-4"
            >
              Já tenho uma conta
            </Button>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="py-8 px-4 border-t-2 border-gum-black bg-gum-white">
        <div className="container mx-auto max-w-6xl">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gum-pink border-2 border-gum-black rounded-md flex items-center justify-center">
                <GraduationCap size={24} strokeWidth={2.5} />
              </div>
              <div>
                <div className="font-black">PPG Hub</div>
                <div className="text-xs font-bold text-gray-600">© 2025 Todos os direitos reservados</div>
              </div>
            </div>

            <div className="flex gap-4">
              <a
                href="https://github.com"
                target="_blank"
                rel="noopener noreferrer"
                className="w-10 h-10 bg-gum-white border-2 border-gum-black rounded-md flex items-center justify-center hover:shadow-brutal transition-all"
              >
                <Github size={20} />
              </a>
              <a
                href="mailto:admin@ppg.edu.br"
                className="w-10 h-10 bg-gum-white border-2 border-gum-black rounded-md flex items-center justify-center hover:shadow-brutal transition-all"
              >
                <Mail size={20} />
              </a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};
