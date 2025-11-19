import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Home } from 'lucide-react';

export const NotFoundPage = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gum-pink via-gum-yellow to-gum-cyan p-4">
      <Card className="w-full max-w-md p-8 text-center">
        <h1 className="text-9xl font-black mb-4">404</h1>
        <h2 className="text-3xl font-black mb-4">Página não encontrada</h2>
        <p className="text-lg font-medium text-gray-600 mb-8">
          A página que você está procurando não existe.
        </p>
        <Link to="/dashboard">
          <Button variant="primary">
            <Home size={18} />
            Voltar para o início
          </Button>
        </Link>
      </Card>
    </div>
  );
};
