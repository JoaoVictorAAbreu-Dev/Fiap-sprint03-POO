# Motiva Sprint 02 - Motor de Regras para Priorização de Roçada

Projeto desenvolvido para a Sprint 02 da disciplina de Programação Orientada a Objetos, com foco na construção de um sistema de monitoramento e priorização de roçada de vegetação em rodovias.

O sistema simula a análise de trechos rodoviários, identifica o comportamento de crescimento da vegetação e gera automaticamente um relatório de prioridade operacional, indicando se o trecho precisa de roçada mecanizada, roçada manual, pulverização preventiva ou nenhuma intervenção imediata.

## Integrantes

| Nome | RM |
|---|---|
| João Victor Alves de Abreu | 564946 |
| Luiz Henrique Barbosa Dias | 562399 |
| Rodrigo Kenshin Viana Matayoshi | 564026 |

## Objetivo da Sprint

Construir o motor de regras responsável por transformar dados de trechos rodoviários em decisões operacionais. A proposta considera que diferentes ambientes possuem diferentes comportamentos de crescimento, como trechos úmidos, secos ou normais, e que cada situação exige um tipo específico de intervenção.

## Funcionalidades implementadas

- Cadastro simulado de trechos de rodovia em array.
- Classificação do comportamento de crescimento da vegetação.
- Cálculo da altura projetada da vegetação.
- Priorização automática dos trechos analisados.
- Indicação de roçada mecanizada, roçada manual, pulverização preventiva ou ausência de intervenção.
- Simulação de trechos monitorados via IoT.
- Geração de relatório operacional no console.
- Execução simulada das intervenções indicadas.

## Conceitos de POO aplicados

### Classe abstrata

A classe `IntervencaoOperacional` representa o conceito genérico de uma intervenção em campo. Ela não pode ser instanciada diretamente, pois uma equipe não executa uma intervenção genérica: ela executa um serviço concreto, como roçada mecanizada, roçada manual ou pulverização.

Classes concretas implementadas:

- `RocadaMecanizada`
- `RocadaManual`
- `Pulverizacao`

### Interface

A interface `MonitoravelViaIoT` define o contrato para objetos capazes de transmitir dados automaticamente por sensor. Com isso, o sistema consegue tratar trechos monitorados por IoT sem acoplar essa funcionalidade diretamente à classe base `TrechoRodovia`.

Classe que implementa a interface:

- `TrechoMonitoradoIoT`

### Herança e polimorfismo

A classe `TrechoMonitoradoIoT` herda de `TrechoRodovia` e implementa `MonitoravelViaIoT`. Além disso, as intervenções concretas herdam de `IntervencaoOperacional` e sobrescrevem o método `executarServico()`.

### Encapsulamento

Os atributos das classes são privados e acessados por métodos públicos, garantindo controle sobre o estado dos objetos e validação dos dados.

## Regras de prioridade

| Condição | Intervenção | Prioridade |
|---|---|---|
| Altura projetada menor que 40 cm | Sem intervenção | Normal |
| Altura projetada entre 40 cm e 59,99 cm | Pulverização preventiva | Média |
| Altura projetada entre 60 cm e 89,99 cm | Roçada manual | Alta |
| Altura projetada a partir de 90 cm | Roçada mecanizada | Crítica |

Trechos classificados como área sensível recebem um acréscimo operacional de risco, aumentando a prioridade da análise.

## Estrutura do projeto

```text
src/
 └── br/com/motiva/
     ├── Main.java
     ├── model/
     │   ├── NivelPrioridade.java
     │   ├── TipoCrescimento.java
     │   ├── TipoIntervencao.java
     │   ├── TrechoMonitoradoIoT.java
     │   └── TrechoRodovia.java
     ├── service/
     │   ├── MotorRegrasPrioridade.java
     │   ├── RelatorioPrioridade.java
     │   └── ResultadoPrioridade.java
     ├── intervencao/
     │   ├── IntervencaoOperacional.java
     │   ├── Pulverizacao.java
     │   ├── RocadaManual.java
     │   └── RocadaMecanizada.java
     ├── iot/
     │   └── MonitoravelViaIoT.java
     └── util/
         └── FormatadorDecimal.java
```

## Como executar no IntelliJ IDEA

1. Abra o IntelliJ IDEA.
2. Selecione `File > Open`.
3. Escolha a pasta do projeto.
4. Aguarde o IntelliJ reconhecer a estrutura Java.
5. Abra o arquivo `src/br/com/motiva/Main.java`.
6. Clique em `Run` no método `main`.

## Como executar pelo terminal

Na raiz do projeto, execute:

```bash
javac -d out $(find src -name "*.java")
java -cp out br.com.motiva.Main
```

No Windows PowerShell, use:

```powershell
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } > sources.txt
javac -d out @sources.txt
java -cp out br.com.motiva.Main
```

## Exemplo de saída esperada

```text
RELATORIO DE PRIORIDADE OPERACIONAL - MOTIVA
KM: 14
Prioridade: CRITICA
Intervencao indicada: Rocada mecanizada
Justificativa: Vegetacao projetada em nivel critico. Indica risco operacional e exige roçada mecanizada.
```

## Respostas de reflexão

### Por que não faz sentido para a Motiva executar apenas uma "Intervenção Operacional" genérica?

Porque uma operação real precisa de procedimento, equipe, equipamento, custo, tempo e risco operacional definidos. Uma intervenção genérica não descreve o que será feito em campo. A classe abstrata serve apenas como modelo comum, enquanto as classes concretas representam serviços reais executáveis.

### Qual a diferença arquitetural entre herdar de uma classe abstrata e implementar uma interface?

A classe abstrata define uma base comum para objetos da mesma família conceitual, podendo conter atributos e comportamentos compartilhados. A interface define apenas um contrato de comportamento, permitindo que classes de diferentes hierarquias implementem uma mesma capacidade. No projeto, `IntervencaoOperacional` é uma abstração base de serviços, enquanto `MonitoravelViaIoT` representa apenas a capacidade de transmitir dados por sensor.

## Observações de Clean Code

- Pacotes organizados por responsabilidade.
- Classes com nomes claros e alinhados ao domínio do problema.
- Métodos pequenos e com responsabilidade única.
- Regras de negócio centralizadas no `MotorRegrasPrioridade`.
- Interface pequena, respeitando o princípio de segregação de interfaces.
- Classe abstrata nomeada como conceito genérico do domínio.

