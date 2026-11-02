import { Admin, Resource } from 'react-admin';
import { Dashboard } from './Dashboard';

export function App() {
  return (
    <Admin dashboard={Dashboard}>
      <Resource name="placeholder" />
    </Admin>
  );
}
