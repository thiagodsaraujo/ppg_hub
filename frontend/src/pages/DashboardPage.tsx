import { useAuthStore } from '@/stores/authStore';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import { LogOut, User } from 'lucide-react';

export const DashboardPage = () => {
  const { user, logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    window.location.href = '/login';
  };

  return (
    <div className="min-h-screen bg-gum-gray-50">
      {/* Header */}
      <header className="bg-gum-white border-b-2 border-gum-black">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-black">PPG Hub</h1>
          <Button variant="outline" onClick={handleLogout}>
            <LogOut size={18} />
            Sair
          </Button>
        </div>
      </header>

      {/* Content */}
      <main className="container mx-auto px-4 py-8">
        <Card className="p-8 max-w-2xl mx-auto">
          <div className="flex items-center gap-4 mb-6">
            <div className="w-16 h-16 bg-gum-pink rounded-full border-2 border-gum-black flex items-center justify-center">
              <User size={32} />
            </div>
            <div>
              <h2 className="text-2xl font-black">Bem-vindo!</h2>
              <p className="text-lg font-medium text-gray-600">{user?.nome}</p>
            </div>
          </div>

          <div className="space-y-4">
            <div className="p-4 bg-gum-yellow border-2 border-gum-black rounded-md">
              <p className="font-bold">Email:</p>
              <p className="text-lg">{user?.email}</p>
            </div>

            <div className="p-4 bg-gum-cyan border-2 border-gum-black rounded-md">
              <p className="font-bold">Roles:</p>
              <div className="flex gap-2 mt-2">
                {user?.roles?.map((role) => (
                  <Badge key={role} variant="default">
                    {role}
                  </Badge>
                ))}
              </div>
            </div>
          </div>
        </Card>
      </main>
    </div>
  );
};
