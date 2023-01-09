class RouteItem extends React.Component
{
    constructor(props)
    {
        super(props);
    }

    render()
    {
        return (
            <li className="route">
                <label className="route-label">Line ID: {this.props.name}<br/>Stops: {this.props.stops}</label>
            </li>
        );
    }
}

class App extends React.Component
{
    constructor(props)
  {
    super(props);

    this.state = { data: [] };

    fetch("http://localhost:8080/data").then(response => {
          console.log(response);
          if (!response.ok)
            throw new Error(`Request failed with status ${response.status}`);
          return response.text();
      }).then(data => {
        console.log(JSON.parse(data));
        this.setState({ data: JSON.parse(data) });
      }).catch(error => console.log(error));
  }
    render() {
        return (
            <ul className="container">
                {this.state.data.map((entry) => <RouteItem name={entry.name} stops={entry.stops}/>)}
            </ul>
        );
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('autogen')
);