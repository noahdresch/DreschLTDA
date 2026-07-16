SELECT 'CREATE DATABASE empresas_jsf WITH ENCODING ''UTF8'' TEMPLATE template0'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'empresas_jsf')\gexec
