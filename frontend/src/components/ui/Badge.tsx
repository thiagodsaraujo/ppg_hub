import { ReactNode } from 'react';
import { clsx } from 'clsx';

interface BadgeProps {
  children: ReactNode;
  variant?: 'default' | 'pink' | 'yellow' | 'cyan';
  className?: string;
}

export const Badge = ({ children, variant = 'default', className }: BadgeProps) => {
  return (
    <span
      className={clsx(
        'badge-brutal',
        variant === 'default' && 'bg-gum-white',
        variant === 'pink' && 'bg-gum-pink',
        variant === 'yellow' && 'bg-gum-yellow',
        variant === 'cyan' && 'bg-gum-cyan',
        className
      )}
    >
      {children}
    </span>
  );
};
