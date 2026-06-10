console.log("Dashboard JS Loaded");
const pending = document.getElementById("pendingCount").value;
const approved = document.getElementById("approvedCount").value;
const rejected = document.getElementById("rejectedCount").value;

new Chart(document.getElementById("statusChart").getContext("2d"),
{
    type: 'pie',
    data: {
        labels:['Pending', 'Approved', 'Rejected'],
        datasets:[{
            data: [pending, approved, rejected],
            backgroundColor:['#ffc107', '#198754', '#dc3545']
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false
    }
}
);